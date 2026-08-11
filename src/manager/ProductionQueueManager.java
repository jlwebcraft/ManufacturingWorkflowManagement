package manager;

import database.DatabaseConnection;
import model.ProductionOrder;
import util.ConsoleFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ProductionQueueManager {

    public static void addOrder(ProductionOrder order) {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("Order Added To Production Queue");
        System.out.println("==========================================");
        System.out.println("Priority : " + order.getPriority());
        System.out.println("Orders Waiting : " + getQueueSize());

    }

    public static ProductionOrder getNextOrder() {

        String selectSql = """
            SELECT po.order_id,
                   po.order_number,
                   po.product_id,
                   po.machine_id,
                   po.quantity,
                   po.created_by,
                   po.priority,
                   po.status
            FROM production_orders po
            INNER JOIN machines m
                ON po.machine_id = m.machine_id
            WHERE po.status = 'PENDING'
              AND m.status = 'AVAILABLE'
            ORDER BY
                CASE po.priority
                    WHEN 'HIGH' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    WHEN 'LOW' THEN 3
                    ELSE 4
                END,
                po.created_at,
                po.order_id
            LIMIT 1
            """;

        String updateSql = """
            UPDATE production_orders
            SET status = 'IN_PROGRESS',
                production_start = COALESCE(production_start, NOW())
            WHERE order_id = ?
              AND status = 'PENDING'
            """;

        try (Connection connection = DatabaseConnection.connectDatabase()) {

            connection.setAutoCommit(false);

            try (
                    PreparedStatement selectStatement = connection.prepareStatement(selectSql);
                    ResultSet resultSet = selectStatement.executeQuery()
            ) {

                if (!resultSet.next()) {
                    connection.rollback();
                    return null;
                }

                ProductionOrder order = new ProductionOrder(
                        resultSet.getInt("order_id"),
                        resultSet.getString("order_number"),
                        resultSet.getInt("product_id"),
                        resultSet.getInt("machine_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("created_by"),
                        resultSet.getString("priority"),
                        "IN_PROGRESS"
                );

                try (PreparedStatement updateStatement = connection.prepareStatement(updateSql)) {

                    updateStatement.setInt(1, order.getOrderId());

                    int rows = updateStatement.executeUpdate();

                    if (rows == 0) {
                        connection.rollback();
                        return null;
                    }

                }

                markMachineBusy(connection, order.getMachineId());
                connection.commit();
                return order;

            } catch (SQLException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);

            }

        } catch (SQLException e) {

            System.out.println("Unable to load next production order.");
            e.printStackTrace();

        }

        return null;

    }

    public static ProductionOrder peekNextOrder() {

        String sql = """
            SELECT po.order_id,
                   po.order_number,
                   po.product_id,
                   po.machine_id,
                   po.quantity,
                   po.created_by,
                   po.priority,
                   po.status
            FROM production_orders po
            INNER JOIN machines m
                ON po.machine_id = m.machine_id
            WHERE po.status = 'PENDING'
              AND m.status = 'AVAILABLE'
            ORDER BY
                CASE po.priority
                    WHEN 'HIGH' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    WHEN 'LOW' THEN 3
                    ELSE 4
                END,
                po.created_at,
                po.order_id
            LIMIT 1
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            if (resultSet.next()) {

                return new ProductionOrder(
                        resultSet.getInt("order_id"),
                        resultSet.getString("order_number"),
                        resultSet.getInt("product_id"),
                        resultSet.getInt("machine_id"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("created_by"),
                        resultSet.getString("priority"),
                        resultSet.getString("status")
                );

            }

        } catch (SQLException e) {

            System.out.println("Unable to load production queue.");
            e.printStackTrace();

        }

        return null;

    }

    public static boolean isQueueEmpty() {
        return getQueueSize() == 0;
    }

    public static int getQueueSize() {

        String sql = """
            SELECT COUNT(*) AS queue_size
            FROM production_orders
            WHERE status = 'PENDING'
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            if (resultSet.next()) {
                return resultSet.getInt("queue_size");
            }

        } catch (SQLException e) {

            System.out.println("Unable to count production queue.");
            e.printStackTrace();

        }

        return 0;

    }

    public static void completeOrder(int orderId) {

        String sql = """
            UPDATE production_orders
            SET status = 'QUALITY_CHECK',
                completed_quantity = quantity,
                production_end = NOW()
            WHERE order_id = ?
            """;

        try (Connection connection = DatabaseConnection.connectDatabase()) {

            connection.setAutoCommit(false);

            try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                preparedStatement.setInt(1, orderId);
                preparedStatement.executeUpdate();
                markMachineAvailable(connection, orderId);
                connection.commit();

            } catch (SQLException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);

            }

        } catch (SQLException e) {

            System.out.println("Unable to complete production order.");
            e.printStackTrace();

        }

    }

    public static void clearQueue() {

        String selectSql = """
            SELECT order_id,
                   product_id,
                   quantity,
                   created_by
            FROM production_orders
            WHERE status = 'PENDING'
            """;

        String updateSql = """
            UPDATE production_orders
            SET status = 'CANCELLED'
            WHERE order_id = ?
            """;

        try (Connection connection = DatabaseConnection.connectDatabase()) {

            connection.setAutoCommit(false);

            try (
                    Statement selectStatement = connection.createStatement();
                    ResultSet resultSet = selectStatement.executeQuery(selectSql);
                    PreparedStatement updateStatement = connection.prepareStatement(updateSql)
            ) {

                int cancelledCount = 0;

                while (resultSet.next()) {

                    ProductionOrderManager.restoreMaterials(
                            connection,
                            resultSet.getInt("product_id"),
                            resultSet.getInt("quantity"),
                            resultSet.getInt("created_by")
                    );

                    updateStatement.setInt(1, resultSet.getInt("order_id"));
                    updateStatement.executeUpdate();
                    cancelledCount++;

                }

                connection.commit();

                System.out.println();
                System.out.println("Production Queue Cleared.");
                System.out.println("Cancelled Orders : " + cancelledCount);
                System.out.println("Inventory restored for cancelled pending orders.");

            } catch (SQLException e) {

                connection.rollback();
                throw e;

            } finally {

                connection.setAutoCommit(true);

            }

        } catch (SQLException e) {

            System.out.println("Unable to clear production queue.");
            e.printStackTrace();

        }

    }

    public static void displayQueue() {

        String sql = """
            SELECT po.order_id,
                   po.order_number,
                   p.product_name,
                   m.machine_name,
                   m.status AS machine_status,
                   po.quantity,
                   po.priority,
                   po.created_at
            FROM production_orders po
            INNER JOIN products p
                ON po.product_id = p.product_id
            LEFT JOIN machines m
                ON po.machine_id = m.machine_id
            WHERE po.status = 'PENDING'
            ORDER BY
                CASE po.priority
                    WHEN 'HIGH' THEN 1
                    WHEN 'MEDIUM' THEN 2
                    WHEN 'LOW' THEN 3
                    ELSE 4
                END,
                po.created_at,
                po.order_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tPRODUCTION QUEUE");
            System.out.println("==========================================");

            boolean found = false;
            int position = 1;

            System.out.println();
            System.out.println(
                    ConsoleFormatter.padRight("No", 5)
                            + ConsoleFormatter.padRight("Order", 20)
                            + ConsoleFormatter.padRight("Product", 28)
                            + ConsoleFormatter.padRight("Machine", 24)
                            + ConsoleFormatter.padRight("Machine Status", 17)
                            + ConsoleFormatter.padRight("Qty", 8)
                            + ConsoleFormatter.padRight("Priority", 12)
                            + "Created At"
            );
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        ConsoleFormatter.padRight(String.valueOf(position++), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("order_number"), 20)
                                + ConsoleFormatter.padRight(resultSet.getString("product_name"), 28)
                                + ConsoleFormatter.padRight(resultSet.getString("machine_name"), 24)
                                + ConsoleFormatter.padRight(resultSet.getString("machine_status"), 17)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("quantity")), 8)
                                + ConsoleFormatter.padRight(resultSet.getString("priority"), 12)
                                + resultSet.getTimestamp("created_at")
                );

            }

            if (!found) {
                System.out.println("Queue is empty.");
            }

            System.out.println();
            System.out.println("Total Orders : " + getQueueSize());

        } catch (SQLException e) {

            System.out.println("Unable to display production queue.");
            e.printStackTrace();

        }

    }

    private static void markMachineBusy(Connection connection, int machineId) throws SQLException {

        if (machineId <= 0) {
            return;
        }

        String sql = """
            UPDATE machines
            SET status = 'BUSY'
            WHERE machine_id = ?
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, machineId);
            preparedStatement.executeUpdate();
        }

    }

    private static void markMachineAvailable(Connection connection, int orderId) throws SQLException {

        String sql = """
            UPDATE machines m
            INNER JOIN production_orders po
                ON m.machine_id = po.machine_id
            SET m.status = 'AVAILABLE'
            WHERE po.order_id = ?
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            preparedStatement.executeUpdate();
        }

    }

}
