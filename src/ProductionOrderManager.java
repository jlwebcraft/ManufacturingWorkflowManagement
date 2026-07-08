import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ProductionOrderManager {

    public static String generateOrderNumber() {

        String date = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        String sql = """
                SELECT MAX(order_id) AS last_id
                FROM production_orders
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        preparedStatement.executeQuery()
        ) {

            int nextNumber = 1;

            if (resultSet.next()) {

                nextNumber = resultSet.getInt("last_id") + 1;

            }

            return "PO-" + date + "-"
                    + String.format("%03d", nextNumber);

        } catch (SQLException e) {

            System.out.println("Unable to generate order number.");
            e.printStackTrace();

        }

        return null;

    }

    public static boolean hasEnoughMaterials(int productId, int orderQuantity) {

        String sql = """
            SELECT
                rm.material_name,
                rm.unit,
                pm.quantity_required,
                i.current_stock
            FROM product_materials pm
            INNER JOIN raw_materials rm
                ON pm.material_id = rm.material_id
            INNER JOIN inventory i
                ON pm.material_id = i.material_id
            WHERE pm.product_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, productId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                boolean hasMaterials = false;

                while (resultSet.next()) {

                    hasMaterials = true;

                    double requiredPerProduct =
                            resultSet.getDouble("quantity_required");

                    double availableStock =
                            resultSet.getDouble("current_stock");

                    double totalRequired =
                            requiredPerProduct * orderQuantity;

                    if (availableStock < totalRequired) {

                        System.out.println();
                        System.out.println("==========================================");
                        System.out.println("\tINSUFFICIENT INVENTORY");
                        System.out.println("==========================================");
                        System.out.println();

                        System.out.println("Material  : "
                                + resultSet.getString("material_name"));

                        System.out.println("Required  : "
                                + totalRequired + " "
                                + resultSet.getString("unit"));

                        System.out.println("Available : "
                                + availableStock + " "
                                + resultSet.getString("unit"));

                        System.out.println();
                        System.out.println("Production Order cannot be created.");

                        return false;

                    }

                }

                if (!hasMaterials) {

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("\tNO BILL OF MATERIALS");
                    System.out.println("==========================================");
                    System.out.println();
                    System.out.println("No raw materials are assigned to this product.");
                    System.out.println("Please configure Product Materials first.");

                    return false;

                }

            }

        } catch (SQLException e) {

            System.out.println("Unable to verify inventory.");
            e.printStackTrace();

            return false;

        }

        return true;

    }

    public static void consumeMaterials(Connection connection, int productId, int orderQuantity, int userId) throws SQLException {

        String selectSql = """
            SELECT
                pm.material_id,
                pm.quantity_required
            FROM product_materials pm
            WHERE pm.product_id = ?
            """;

        String updateInventorySql = """
            UPDATE inventory
            SET current_stock = current_stock - ?
            WHERE material_id = ?
            """;

        String transactionSql = """
            INSERT INTO material_transactions
            (material_id,
             transaction_type,
             quantity,
             reason,
             performed_by)
            VALUES (?, ?, ?, ?, ?)
            """;

        try {

            PreparedStatement selectStatement =
                    connection.prepareStatement(selectSql);

            selectStatement.setInt(1, productId);

            ResultSet resultSet = selectStatement.executeQuery();

            while (resultSet.next()) {

                int materialId =
                        resultSet.getInt("material_id");

                double quantityRequired =
                        resultSet.getDouble("quantity_required");

                double totalRequired =
                        quantityRequired * orderQuantity;

                PreparedStatement inventoryStatement =
                        connection.prepareStatement(updateInventorySql);

                inventoryStatement.setDouble(1, totalRequired);
                inventoryStatement.setInt(2, materialId);

                inventoryStatement.executeUpdate();

                PreparedStatement transactionStatement =
                        connection.prepareStatement(transactionSql);

                transactionStatement.setInt(1, materialId);
                transactionStatement.setString(2, "OUT");
                transactionStatement.setDouble(3, totalRequired);
                transactionStatement.setString(4, "Production Order");
                transactionStatement.setInt(5, userId);

                transactionStatement.executeUpdate();

                inventoryStatement.close();
                transactionStatement.close();

            }

            resultSet.close();
            selectStatement.close();

        } catch (SQLException e) {
            throw e;
        }

    }

    public static void addProductionOrder(ProductionOrder order) {

        String orderNumber = generateOrderNumber();

        String sql = """
            INSERT INTO production_orders
            (order_number,
             product_id,
             quantity,
             machine_id,
             created_by,
             status)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.connectDatabase();

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement =
                    connection.prepareStatement(sql);

            preparedStatement.setString(1, orderNumber);
            preparedStatement.setInt(2, order.getProductId());
            preparedStatement.setInt(3, order.getQuantity());
            preparedStatement.setInt(4, order.getMachineId());
            preparedStatement.setInt(5, order.getCreatedBy());
            preparedStatement.setString(6, "PENDING");

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                consumeMaterials(
                        connection,
                        order.getProductId(),
                        order.getQuantity(),
                        order.getCreatedBy()
                );

                connection.commit();

                System.out.println();
                System.out.println("==========================================");
                System.out.println("Production Order Created");
                System.out.println("==========================================");
                System.out.println("Order Number : " + orderNumber);
                System.out.println("Status : PENDING");

            } else {

                connection.rollback();

            }

            preparedStatement.close();

        } catch (SQLException e) {

            try {

                if (connection != null) {

                    connection.rollback();

                }

            } catch (SQLException ex) {

                ex.printStackTrace();

            }

            System.out.println("Unable to create Production Order.");
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

    public static void viewProductionOrders() {

        String sql = """
        SELECT
            po.order_id,
            po.order_number,
            p.product_name,
            po.quantity,
            m.machine_name,
            u.name,
            po.status,
            po.created_at
        FROM production_orders po
        INNER JOIN products p
            ON po.product_id = p.product_id
        INNER JOIN machines m
            ON po.machine_id = m.machine_id
        INNER JOIN users u
            ON po.created_by = u.user_id
        ORDER BY po.order_id
        """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        preparedStatement.executeQuery()
        ) {

            boolean found = false;

            System.out.println();
            System.out.println("==================================================");
            System.out.println("\t\tPRODUCTION ORDERS");
            System.out.println("==================================================");

            while (resultSet.next()) {

                found = true;

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("Order ID        : " + resultSet.getInt("order_id"));
                System.out.println("Order Number    : " + resultSet.getString("order_number"));
                System.out.println("Product         : " + resultSet.getString("product_name"));
                System.out.println("Quantity        : " + resultSet.getInt("quantity"));
                System.out.println("Machine         : " + resultSet.getString("machine_name"));
                System.out.println("Created By      : " + resultSet.getString("name"));
                System.out.println("Status          : " + resultSet.getString("status"));
                System.out.println("Created At      : " + resultSet.getTimestamp("created_at"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println();
                System.out.println("No production orders found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to load production orders.");
            e.printStackTrace();

        }

    }

    public static boolean productionOrderExists(int orderId) {

        String sql = """
            SELECT order_id
            FROM production_orders
            WHERE order_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, orderId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void updateOrderStatus(int orderId, String status) {

        String sql = """
            UPDATE production_orders
            SET status = ?
            WHERE order_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, orderId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println();
                System.out.println("Production Order Status Updated.");
                System.out.println("New Status : " + status);

            } else {

                System.out.println("Invalid Order ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to update order status.");
            e.printStackTrace();

        }

    }

    public static void cancelProductionOrder(int orderId) {

        String orderSql = """
            SELECT
                product_id,
                quantity,
                created_by,
                status
            FROM production_orders
            WHERE order_id = ?
            """;

        String updateStatusSql = """
            UPDATE production_orders
            SET status = 'CANCELLED'
            WHERE order_id = ?
            """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.connectDatabase();

            connection.setAutoCommit(false);

            PreparedStatement orderStatement =
                    connection.prepareStatement(orderSql);

            orderStatement.setInt(1, orderId);

            ResultSet resultSet = orderStatement.executeQuery();

            if (!resultSet.next()) {

                System.out.println("Invalid Order ID.");

                resultSet.close();
                orderStatement.close();

                connection.rollback();
                return;

            }

            String currentStatus = resultSet.getString("status");

            if (currentStatus.equals("CANCELLED")) {

                System.out.println("Production Order is already cancelled.");

                resultSet.close();
                orderStatement.close();

                connection.rollback();
                return;

            }

            if (!currentStatus.equals("PENDING")) {

                System.out.println("Only PENDING production orders can be cancelled.");

                resultSet.close();
                orderStatement.close();

                connection.rollback();
                return;

            }

            int productId = resultSet.getInt("product_id");
            int quantity = resultSet.getInt("quantity");
            int userId = resultSet.getInt("created_by");

            resultSet.close();
            orderStatement.close();

            restoreMaterials(
                    connection,
                    productId,
                    quantity,
                    userId
            );

            PreparedStatement updateStatement =
                    connection.prepareStatement(updateStatusSql);

            updateStatement.setInt(1, orderId);

            updateStatement.executeUpdate();

            updateStatement.close();

            connection.commit();

            System.out.println();
            System.out.println("==========================================");
            System.out.println("Production Order Cancelled");
            System.out.println("==========================================");
            System.out.println("Inventory Restored Successfully.");

        } catch (SQLException e) {

            try {

                if (connection != null) {

                    connection.rollback();

                }

            } catch (SQLException ex) {

                ex.printStackTrace();

            }

            System.out.println("Unable to cancel production order.");
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

    public static void restoreMaterials(Connection connection,
                                        int productId,
                                        int orderQuantity,
                                        int userId) throws SQLException {

        String selectSql = """
            SELECT
                pm.material_id,
                pm.quantity_required
            FROM product_materials pm
            WHERE pm.product_id = ?
            """;

        String updateInventorySql = """
            UPDATE inventory
            SET current_stock = current_stock + ?
            WHERE material_id = ?
            """;

        String transactionSql = """
            INSERT INTO material_transactions
            (material_id,
             transaction_type,
             quantity,
             reason,
             performed_by)
            VALUES (?, ?, ?, ?, ?)
            """;

        PreparedStatement selectStatement =
                connection.prepareStatement(selectSql);

        selectStatement.setInt(1, productId);

        ResultSet resultSet = selectStatement.executeQuery();

        while (resultSet.next()) {

            int materialId =
                    resultSet.getInt("material_id");

            double quantityRequired =
                    resultSet.getDouble("quantity_required");

            double totalRequired =
                    quantityRequired * orderQuantity;

            PreparedStatement inventoryStatement =
                    connection.prepareStatement(updateInventorySql);

            inventoryStatement.setDouble(1, totalRequired);
            inventoryStatement.setInt(2, materialId);

            inventoryStatement.executeUpdate();

            PreparedStatement transactionStatement =
                    connection.prepareStatement(transactionSql);

            transactionStatement.setInt(1, materialId);
            transactionStatement.setString(2, "IN");
            transactionStatement.setDouble(3, totalRequired);
            transactionStatement.setString(4, "Production Order Cancelled");
            transactionStatement.setInt(5, userId);

            transactionStatement.executeUpdate();

            inventoryStatement.close();
            transactionStatement.close();

        }

        resultSet.close();
        selectStatement.close();

    }

    public static void searchProductionOrder(String keyword) {

        String sql = """
        SELECT
            po.order_id,
            po.order_number,
            p.product_name,
            po.quantity,
            m.machine_name,
            u.name,
            po.status,
            po.created_at
        FROM production_orders po
        INNER JOIN products p
            ON po.product_id = p.product_id
        INNER JOIN machines m
            ON po.machine_id = m.machine_id
        INNER JOIN users u
            ON po.created_by = u.user_id
        WHERE po.order_number LIKE ?
        ORDER BY po.order_id
        """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, "%" + keyword + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean found = false;

            System.out.println();
            System.out.println("==================================================");
            System.out.println("\t\tSEARCH RESULTS");
            System.out.println("==================================================");

            while (resultSet.next()) {

                found = true;

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("Order ID        : " + resultSet.getInt("order_id"));
                System.out.println("Order Number    : " + resultSet.getString("order_number"));
                System.out.println("Product         : " + resultSet.getString("product_name"));
                System.out.println("Quantity        : " + resultSet.getInt("quantity"));
                System.out.println("Machine         : " + resultSet.getString("machine_name"));
                System.out.println("Created By      : " + resultSet.getString("name"));
                System.out.println("Status          : " + resultSet.getString("status"));
                System.out.println("Created At      : " + resultSet.getTimestamp("created_at"));
                System.out.println("--------------------------------------------------");

            }

            resultSet.close();

            if (!found) {

                System.out.println("No matching production order found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to search production order.");
            e.printStackTrace();

        }

    }

    public static void showProductionOrderList() {

        String sql = """
        SELECT order_id,
               order_number
        FROM production_orders
        ORDER BY order_id
        """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tPRODUCTION ORDERS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tOrder Number");
            System.out.println("------------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        ConsoleFormatter.padRight(
                                String.valueOf(resultSet.getInt("order_id")), 5)
                                + resultSet.getString("order_number"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load production orders.");
            e.printStackTrace();

        }

    }

}