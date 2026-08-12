package manager;

import database.DatabaseConnection;
import util.ConsoleFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DeliveryManager {

    public static void showDeliverableOrders() {
        String sql = """
            SELECT po.order_id,
                   po.order_number,
                   p.product_name,
                   po.quantity,
                   COALESCE(fgi.available_quantity, 0) AS available_quantity,
                   po.status
            FROM production_orders po
            INNER JOIN products p
                ON po.product_id = p.product_id
            LEFT JOIN finished_goods_inventory fgi
                ON po.product_id = fgi.product_id
            WHERE po.status = 'COMPLETED'
              AND NOT EXISTS (
                  SELECT 1
                  FROM deliveries d
                  WHERE d.order_id = po.order_id
              )
            ORDER BY po.production_end DESC, po.order_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            boolean found = false;

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tORDERS READY FOR DELIVERY");
            System.out.println("==========================================");
            System.out.println();
            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Order No", 20)
                            + ConsoleFormatter.padRight("Product", 30)
                            + ConsoleFormatter.padRight("Qty", 8)
                            + ConsoleFormatter.padRight("Stock", 8)
                            + "Status"
            );
            System.out.println("--------------------------------------------------------------------------------");

            while (resultSet.next()) {
                found = true;
                System.out.println(
                        ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("order_id")), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("order_number"), 20)
                                + ConsoleFormatter.padRight(resultSet.getString("product_name"), 30)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("quantity")), 8)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("available_quantity")), 8)
                                + resultSet.getString("status")
                );
            }

            if (!found) {
                System.out.println("No completed orders are ready for delivery.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to load deliverable orders.");
            e.printStackTrace();
        }
    }

    public static boolean orderReadyForDelivery(int orderId) {
        String sql = """
            SELECT po.order_id
            FROM production_orders po
            WHERE po.order_id = ?
              AND po.status = 'COMPLETED'
              AND NOT EXISTS (
                  SELECT 1
                  FROM deliveries d
                  WHERE d.order_id = po.order_id
              )
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, orderId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Unable to validate delivery order.");
            e.printStackTrace();
        }

        return false;
    }

    public static void deliverOrder(int orderId,
                                    int deliveredBy,
                                    String deliveredTo,
                                    String deliveryAddress,
                                    String remarks) {
        String orderSql = """
            SELECT po.order_id,
                   po.order_number,
                   po.product_id,
                   po.quantity,
                   po.status,
                   p.product_name,
                   COALESCE(fgi.available_quantity, 0) AS available_quantity
            FROM production_orders po
            INNER JOIN products p
                ON po.product_id = p.product_id
            LEFT JOIN finished_goods_inventory fgi
                ON po.product_id = fgi.product_id
            WHERE po.order_id = ?
              AND po.status = 'COMPLETED'
              AND NOT EXISTS (
                  SELECT 1
                  FROM deliveries d
                  WHERE d.order_id = po.order_id
              )
            """;

        String stockSql = """
            UPDATE finished_goods_inventory
            SET available_quantity = available_quantity - ?
            WHERE product_id = ?
              AND available_quantity >= ?
            """;

        String deliverySql = """
            INSERT INTO deliveries
            (order_id, delivered_by, delivered_quantity, delivered_to, delivery_address, remarks)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        String statusSql = """
            UPDATE production_orders
            SET status = 'DELIVERED'
            WHERE order_id = ?
            """;

        String historySql = """
            INSERT INTO workflow_history
            (order_id, previous_status, new_status, changed_by, remarks)
            VALUES (?, 'COMPLETED', 'DELIVERED', ?, ?)
            """;

        Connection connection = null;

        try {
            connection = DatabaseConnection.connectDatabase();
            connection.setAutoCommit(false);

            String orderNumber;
            String productName;
            int productId;
            int quantity;
            int availableQuantity;

            try (PreparedStatement orderStatement = connection.prepareStatement(orderSql)) {
                orderStatement.setInt(1, orderId);

                try (ResultSet resultSet = orderStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        connection.rollback();
                        System.out.println("Only completed and undelivered orders can be delivered.");
                        return;
                    }

                    orderNumber = resultSet.getString("order_number");
                    productName = resultSet.getString("product_name");
                    productId = resultSet.getInt("product_id");
                    quantity = resultSet.getInt("quantity");
                    availableQuantity = resultSet.getInt("available_quantity");
                }
            }

            if (availableQuantity < quantity) {
                connection.rollback();
                System.out.println("Finished goods stock is not enough for this delivery.");
                System.out.println("Required : " + quantity);
                System.out.println("Available: " + availableQuantity);
                return;
            }

            try (PreparedStatement stockStatement = connection.prepareStatement(stockSql)) {
                stockStatement.setInt(1, quantity);
                stockStatement.setInt(2, productId);
                stockStatement.setInt(3, quantity);

                if (stockStatement.executeUpdate() == 0) {
                    connection.rollback();
                    System.out.println("Finished goods stock is not enough for this delivery.");
                    return;
                }
            }

            try (PreparedStatement deliveryStatement = connection.prepareStatement(deliverySql)) {
                deliveryStatement.setInt(1, orderId);
                deliveryStatement.setInt(2, deliveredBy);
                deliveryStatement.setInt(3, quantity);
                deliveryStatement.setString(4, deliveredTo);
                deliveryStatement.setString(5, deliveryAddress);
                deliveryStatement.setString(6, remarks);
                deliveryStatement.executeUpdate();
            }

            try (PreparedStatement statusStatement = connection.prepareStatement(statusSql)) {
                statusStatement.setInt(1, orderId);
                statusStatement.executeUpdate();
            }

            try (PreparedStatement historyStatement = connection.prepareStatement(historySql)) {
                historyStatement.setInt(1, orderId);
                historyStatement.setInt(2, deliveredBy);
                historyStatement.setString(3, "Order delivered to " + deliveredTo + ".");
                historyStatement.executeUpdate();
            }

            connection.commit();

            System.out.println();
            System.out.println("==========================================");
            System.out.println("Order Delivered Successfully");
            System.out.println("==========================================");
            System.out.println("Order Number : " + orderNumber);
            System.out.println("Product      : " + productName);
            System.out.println("Quantity     : " + quantity);
            System.out.println("Delivered To : " + deliveredTo);
            System.out.println("Status       : DELIVERED");
        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println("Unable to deliver order.");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.setAutoCommit(true);
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void viewDeliveries() {
        String sql = """
            SELECT d.delivery_id,
                   po.order_number,
                   p.product_name,
                   d.delivered_quantity,
                   d.delivered_to,
                   u.name AS delivered_by,
                   d.delivery_date
            FROM deliveries d
            INNER JOIN production_orders po
                ON d.order_id = po.order_id
            INNER JOIN products p
                ON po.product_id = p.product_id
            INNER JOIN users u
                ON d.delivered_by = u.user_id
            ORDER BY d.delivery_date DESC, d.delivery_id DESC
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            boolean found = false;

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tDELIVERY RECORDS");
            System.out.println("==========================================");
            System.out.println();
            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Order", 20)
                            + ConsoleFormatter.padRight("Product", 28)
                            + ConsoleFormatter.padRight("Qty", 8)
                            + ConsoleFormatter.padRight("Delivered To", 24)
                            + ConsoleFormatter.padRight("By", 24)
                            + "Date"
            );
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                found = true;
                System.out.println(
                        ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("delivery_id")), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("order_number"), 20)
                                + ConsoleFormatter.padRight(resultSet.getString("product_name"), 28)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("delivered_quantity")), 8)
                                + ConsoleFormatter.padRight(resultSet.getString("delivered_to"), 24)
                                + ConsoleFormatter.padRight(resultSet.getString("delivered_by"), 24)
                                + resultSet.getTimestamp("delivery_date")
                );
            }

            if (!found) {
                System.out.println("No delivery records found.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to load deliveries.");
            e.printStackTrace();
        }
    }
}
