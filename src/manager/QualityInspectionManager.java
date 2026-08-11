package manager;

import database.DatabaseConnection;
import util.ConsoleFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class QualityInspectionManager {

    public static void showOrdersReadyForInspection() {

        String sql = """
            SELECT po.order_id,
                   po.order_number,
                   p.product_name,
                   po.quantity,
                   po.completed_quantity,
                   po.status
            FROM production_orders po
            INNER JOIN products p ON po.product_id = p.product_id
            WHERE po.status = 'QUALITY_CHECK'
              AND NOT EXISTS (
                  SELECT 1
                  FROM quality_inspections qi
                  WHERE qi.order_id = po.order_id
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
            System.out.println("\tORDERS READY FOR INSPECTION");
            System.out.println("==========================================");
            System.out.println();
            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Order No", 20)
                            + ConsoleFormatter.padRight("Product", 30)
                            + ConsoleFormatter.padRight("Qty", 8)
                            + ConsoleFormatter.padRight("Done", 8)
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
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("completed_quantity")), 8)
                                + resultSet.getString("status")
                );
            }

            if (!found) {
                System.out.println("No quality-check orders are ready for inspection.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to load orders ready for inspection.");
            e.printStackTrace();
        }
    }

    public static void showQualityInspectorList() {

        String sql = """
            SELECT u.user_id, u.name, r.role_name
            FROM users u
            INNER JOIN roles r ON u.role_id = r.role_id
            WHERE u.status = 'ACTIVE'
              AND r.role_name = 'Quality Inspector'
            ORDER BY u.user_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            boolean found = false;

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tQUALITY INSPECTORS");
            System.out.println("==========================================");
            System.out.println();
            System.out.println(ConsoleFormatter.padRight("ID", 5)
                    + ConsoleFormatter.padRight("Name", 28)
                    + "Role");
            System.out.println("------------------------------------------------------------");

            while (resultSet.next()) {
                found = true;
                System.out.println(ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("user_id")), 5)
                        + ConsoleFormatter.padRight(resultSet.getString("name"), 28)
                        + resultSet.getString("role_name"));
            }

            if (!found) {
                System.out.println("No active quality inspectors found.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to load quality inspectors.");
            e.printStackTrace();
        }
    }

    public static boolean orderReadyForInspection(int orderId) {

        String sql = """
            SELECT order_id
            FROM production_orders
            WHERE order_id = ?
              AND status = 'QUALITY_CHECK'
              AND NOT EXISTS (
                  SELECT 1
                  FROM quality_inspections qi
                  WHERE qi.order_id = production_orders.order_id
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
            System.out.println("Unable to validate production order.");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean qualityInspectorExists(int inspectorId) {

        String sql = """
            SELECT u.user_id
            FROM users u
            INNER JOIN roles r ON u.role_id = r.role_id
            WHERE u.user_id = ?
              AND u.status = 'ACTIVE'
              AND r.role_name = 'Quality Inspector'
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, inspectorId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Unable to validate quality inspector.");
            e.printStackTrace();
        }

        return false;
    }

    public static void addInspection(int orderId,
                                     int inspectorId,
                                     String result,
                                     int defectiveQuantity,
                                     String remarks) {

        if (result.equals("PASS")) {
            defectiveQuantity = 0;
        }

        Connection connection = null;

        try {
            connection = DatabaseConnection.connectDatabase();
            connection.setAutoCommit(false);

            OrderDetails orderDetails = loadQualityCheckOrder(connection, orderId);

            if (orderDetails == null) {
                connection.rollback();
                System.out.println("Only QUALITY_CHECK production orders can be inspected.");
                return;
            }

            if (defectiveQuantity < 0 || defectiveQuantity > orderDetails.quantity) {
                connection.rollback();
                System.out.println("Defective quantity must be between 0 and order quantity.");
                return;
            }

            if (!result.equals("PASS") && defectiveQuantity == 0) {
                connection.rollback();
                System.out.println("FAIL or REWORK inspection must include defective quantity.");
                return;
            }

            insertInspection(connection, orderId, inspectorId, result, defectiveQuantity, remarks);

            int approvedQuantity = orderDetails.quantity - defectiveQuantity;
            if (approvedQuantity > 0) {
                updateFinishedGoodsInventory(connection, orderDetails.productId, approvedQuantity);
            }

            markOrderCompleted(connection, orderId);
            insertWorkflowHistory(connection, orderId, orderDetails.status, "COMPLETED", inspectorId,
                    "Quality inspection result: " + result + ". Approved quantity: " + approvedQuantity + ".");

            Integer replacementOrderId = null;
            String replacementOrderNumber = null;

            if (!result.equals("PASS") && defectiveQuantity > 0) {
                if (!hasEnoughMaterials(connection, orderDetails.productId, defectiveQuantity)) {
                    connection.rollback();
                    System.out.println("Inspection was not saved because inventory is insufficient for the replacement order.");
                    return;
                }

                replacementOrderNumber = generateOrderNumber(connection);
                replacementOrderId = createReplacementOrder(
                        connection,
                        replacementOrderNumber,
                        orderDetails.productId,
                        orderDetails.machineId,
                        defectiveQuantity,
                        orderDetails.createdBy,
                        orderDetails.priority
                );

                ProductionOrderManager.consumeMaterials(
                        connection,
                        replacementOrderId,
                        orderDetails.productId,
                        defectiveQuantity,
                        orderDetails.createdBy
                );

                insertWorkflowHistory(connection, replacementOrderId, null, "PENDING", inspectorId,
                        "Replacement order created automatically after failed inspection of "
                                + orderDetails.orderNumber + ".");
            }

            connection.commit();

            System.out.println();
            System.out.println("==========================================");
            System.out.println("Quality Inspection Saved");
            System.out.println("==========================================");
            System.out.println("Order Number       : " + orderDetails.orderNumber);
            System.out.println("Result             : " + result);
            System.out.println("Approved Quantity  : " + approvedQuantity);
            System.out.println("Defective Quantity : " + defectiveQuantity);
            System.out.println("Order Status       : COMPLETED");

            if (replacementOrderId != null) {
                System.out.println("Replacement Order  : " + replacementOrderNumber);
                System.out.println("Replacement Status : PENDING");
                System.out.println("Added To Production Queue");
            }

        } catch (SQLException e) {
            try {
                if (connection != null) {
                    connection.rollback();
                }
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
            System.out.println("Unable to save quality inspection.");
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

    public static void viewInspections() {

        String sql = """
            SELECT qi.inspection_id,
                   po.order_number,
                   p.product_name,
                   u.name AS inspector_name,
                   qi.inspection_date,
                   qi.result,
                   qi.defective_quantity,
                   qi.remarks
            FROM quality_inspections qi
            INNER JOIN production_orders po ON qi.order_id = po.order_id
            INNER JOIN products p ON po.product_id = p.product_id
            INNER JOIN users u ON qi.inspector_id = u.user_id
            ORDER BY qi.inspection_date DESC, qi.inspection_id DESC
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            boolean found = false;

            System.out.println();
            System.out.println("==================================================");
            System.out.println("\t\tQUALITY INSPECTIONS");
            System.out.println("==================================================");
            System.out.println();
            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Order", 20)
                            + ConsoleFormatter.padRight("Product", 28)
                            + ConsoleFormatter.padRight("Inspector", 24)
                            + ConsoleFormatter.padRight("Date", 14)
                            + ConsoleFormatter.padRight("Result", 10)
                            + ConsoleFormatter.padRight("Defect", 8)
                            + "Remarks"
            );
            System.out.println("----------------------------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                found = true;
                System.out.println(
                        ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("inspection_id")), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("order_number"), 20)
                                + ConsoleFormatter.padRight(resultSet.getString("product_name"), 28)
                                + ConsoleFormatter.padRight(resultSet.getString("inspector_name"), 24)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getDate("inspection_date")), 14)
                                + ConsoleFormatter.padRight(resultSet.getString("result"), 10)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("defective_quantity")), 8)
                                + resultSet.getString("remarks")
                );
            }

            if (!found) {
                System.out.println("No quality inspections found.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to load quality inspections.");
            e.printStackTrace();
        }
    }

    private static OrderDetails loadQualityCheckOrder(Connection connection, int orderId) throws SQLException {

        String sql = """
            SELECT order_id,
                   order_number,
                   product_id,
                   machine_id,
                   quantity,
                   created_by,
                   priority,
                   status
            FROM production_orders
            WHERE order_id = ?
              AND status = 'QUALITY_CHECK'
              AND NOT EXISTS (
                  SELECT 1
                  FROM quality_inspections qi
                  WHERE qi.order_id = production_orders.order_id
              )
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (!resultSet.next()) {
                    return null;
                }

                return new OrderDetails(
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
        }
    }

    private static void insertInspection(Connection connection,
                                         int orderId,
                                         int inspectorId,
                                         String result,
                                         int defectiveQuantity,
                                         String remarks) throws SQLException {

        String sql = """
            INSERT INTO quality_inspections
            (order_id, inspector_id, inspection_date, result, defective_quantity, remarks)
            VALUES (?, ?, CURDATE(), ?, ?, ?)
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            preparedStatement.setInt(2, inspectorId);
            preparedStatement.setString(3, result);
            preparedStatement.setInt(4, defectiveQuantity);
            preparedStatement.setString(5, remarks);
            preparedStatement.executeUpdate();
        }
    }

    private static void updateFinishedGoodsInventory(Connection connection,
                                                     int productId,
                                                     int approvedQuantity) throws SQLException {

        String sql = """
            INSERT INTO finished_goods_inventory (product_id, available_quantity)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE
                available_quantity = available_quantity + VALUES(available_quantity)
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);
            preparedStatement.setInt(2, approvedQuantity);
            preparedStatement.executeUpdate();
        }
    }

    private static void markOrderCompleted(Connection connection, int orderId) throws SQLException {

        String sql = """
            UPDATE production_orders
            SET status = 'COMPLETED'
            WHERE order_id = ?
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            preparedStatement.executeUpdate();
        }
    }

    private static boolean hasEnoughMaterials(Connection connection,
                                              int productId,
                                              int quantity) throws SQLException {

        String sql = """
            SELECT rm.material_name,
                   pm.quantity_required,
                   COALESCE(i.current_stock, 0) AS current_stock
            FROM product_materials pm
            INNER JOIN raw_materials rm ON pm.material_id = rm.material_id
            LEFT JOIN inventory i ON pm.material_id = i.material_id
            WHERE pm.product_id = ?
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, productId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                boolean hasMaterials = false;

                while (resultSet.next()) {
                    hasMaterials = true;
                    double totalRequired = resultSet.getDouble("quantity_required") * quantity;
                    double currentStock = resultSet.getDouble("current_stock");

                    if (currentStock < totalRequired) {
                        System.out.println("Insufficient stock for " + resultSet.getString("material_name") + ".");
                        return false;
                    }
                }

                return hasMaterials;
            }
        }
    }

    private static String generateOrderNumber(Connection connection) throws SQLException {
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String sql = """
            SELECT COALESCE(MAX(order_id), 0) + 1 AS next_id
            FROM production_orders
            """;

        try (
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            int nextNumber = 1;
            if (resultSet.next()) {
                nextNumber = resultSet.getInt("next_id");
            }
            return "PO-" + date + "-" + String.format("%03d", nextNumber);
        }
    }

    private static int createReplacementOrder(Connection connection,
                                              String orderNumber,
                                              int productId,
                                              int machineId,
                                              int quantity,
                                              int createdBy,
                                              String priority) throws SQLException {

        String sql = """
            INSERT INTO production_orders
            (order_number, product_id, machine_id, quantity, created_by, priority, status)
            VALUES (?, ?, ?, ?, ?, ?, 'PENDING')
            """;

        try (PreparedStatement preparedStatement =
                     connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, orderNumber);
            preparedStatement.setInt(2, productId);
            preparedStatement.setInt(3, machineId);
            preparedStatement.setInt(4, quantity);
            preparedStatement.setInt(5, createdBy);
            preparedStatement.setString(6, priority);
            preparedStatement.executeUpdate();

            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        }

        throw new SQLException("Unable to create replacement production order.");
    }

    private static void insertWorkflowHistory(Connection connection,
                                              int orderId,
                                              String previousStatus,
                                              String newStatus,
                                              int changedBy,
                                              String remarks) throws SQLException {

        String sql = """
            INSERT INTO workflow_history
            (order_id, previous_status, new_status, changed_by, remarks)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, orderId);
            preparedStatement.setString(2, previousStatus);
            preparedStatement.setString(3, newStatus);
            preparedStatement.setInt(4, changedBy);
            preparedStatement.setString(5, remarks);
            preparedStatement.executeUpdate();
        }
    }

    private static class OrderDetails {
        private final int orderId;
        private final String orderNumber;
        private final int productId;
        private final int machineId;
        private final int quantity;
        private final int createdBy;
        private final String priority;
        private final String status;

        private OrderDetails(int orderId,
                             String orderNumber,
                             int productId,
                             int machineId,
                             int quantity,
                             int createdBy,
                             String priority,
                             String status) {
            this.orderId = orderId;
            this.orderNumber = orderNumber;
            this.productId = productId;
            this.machineId = machineId;
            this.quantity = quantity;
            this.createdBy = createdBy;
            this.priority = priority;
            this.status = status;
        }
    }
}