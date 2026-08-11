package manager;

import database.DatabaseConnection;
import model.ProductionOrder;
import util.ConsoleFormatter;

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

    public static void consumeMaterials(Connection connection,
                                        int orderId,
                                        int productId,
                                        int orderQuantity,
                                        int userId) throws SQLException {

        String selectSql = """
            SELECT pm.material_id,
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
            (material_id, transaction_type, quantity, reason, performed_by)
            VALUES (?, ?, ?, ?, ?)
            """;

        String usageSql = """
            INSERT INTO production_material_usage
            (order_id, material_id, quantity_used)
            VALUES (?, ?, ?)
            """;

        try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {

            selectStatement.setInt(1, productId);

            try (ResultSet resultSet = selectStatement.executeQuery()) {

                while (resultSet.next()) {

                    int materialId = resultSet.getInt("material_id");
                    double quantityRequired = resultSet.getDouble("quantity_required");
                    double totalRequired = quantityRequired * orderQuantity;

                    try (PreparedStatement inventoryStatement = connection.prepareStatement(updateInventorySql);
                         PreparedStatement transactionStatement = connection.prepareStatement(transactionSql);
                         PreparedStatement usageStatement = connection.prepareStatement(usageSql)) {

                        inventoryStatement.setDouble(1, totalRequired);
                        inventoryStatement.setInt(2, materialId);
                        inventoryStatement.executeUpdate();

                        transactionStatement.setInt(1, materialId);
                        transactionStatement.setString(2, "OUT");
                        transactionStatement.setDouble(3, totalRequired);
                        transactionStatement.setString(4, "Production Order");
                        transactionStatement.setInt(5, userId);
                        transactionStatement.executeUpdate();

                        usageStatement.setInt(1, orderId);
                        usageStatement.setInt(2, materialId);
                        usageStatement.setDouble(3, totalRequired);
                        usageStatement.executeUpdate();

                    }

                }

            }

        }

    }
    public static void addProductionOrder(ProductionOrder order) {

        String orderNumber = generateOrderNumber();

        String sql = """
        INSERT INTO production_orders
        (order_number,
         product_id,
         machine_id,
         quantity,
         created_by,
         priority,
         status)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.connectDatabase();

            connection.setAutoCommit(false);

            PreparedStatement preparedStatement = connection.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, orderNumber);
            preparedStatement.setInt(2, order.getProductId());
            preparedStatement.setInt(3, order.getMachineId());
            preparedStatement.setInt(4, order.getQuantity());
            preparedStatement.setInt(5, order.getCreatedBy());
            preparedStatement.setString(6, order.getPriority());
            preparedStatement.setString(7, "PENDING");

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();

                if (generatedKeys.next()) {
                    order.setOrderId(generatedKeys.getInt(1));
                }

                generatedKeys.close();

                consumeMaterials(
                        connection,
                        order.getOrderId(),
                        order.getProductId(),
                        order.getQuantity(),
                        order.getCreatedBy()
                );

                connection.commit();

                order.setOrderNumber(orderNumber);
                order.setStatus("PENDING");

                ProductionQueueManager.addOrder(order);

                System.out.println();
                System.out.println("==========================================");
                System.out.println("Production Order Created Successfully");
                System.out.println("==========================================");
                System.out.println("Order Number : " + orderNumber);
                System.out.println("Priority     : " + order.getPriority());
                System.out.println("Status       : PENDING");
                System.out.println("Added To Production Queue");
                System.out.println("Current Queue Size : "
                        + ProductionQueueManager.getQueueSize());

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
            po.priority,
            po.status,
            po.created_at
        FROM production_orders po
        INNER JOIN products p
            ON po.product_id = p.product_id
        INNER JOIN users u
            ON po.created_by = u.user_id
        LEFT JOIN machines m
            ON po.machine_id = m.machine_id
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
                System.out.println("Priority        : " + resultSet.getString("priority"));
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

        String selectSql = """
            SELECT status,
                   machine_id,
                   created_by
            FROM production_orders
            WHERE order_id = ?
            """;

        String startSql = """
            UPDATE production_orders
            SET status = 'IN_PROGRESS',
                production_start = COALESCE(production_start, NOW())
            WHERE order_id = ?
              AND status = 'PENDING'
            """;

        String qualityCheckSql = """
            UPDATE production_orders
            SET status = 'QUALITY_CHECK',
                completed_quantity = quantity,
                production_end = NOW()
            WHERE order_id = ?
              AND status = 'IN_PROGRESS'
            """;

        String busyMachineSql = """
            UPDATE machines
            SET status = 'BUSY'
            WHERE machine_id = ?
              AND status = 'AVAILABLE'
            """;

        String availableMachineSql = """
            UPDATE machines
            SET status = 'AVAILABLE'
            WHERE machine_id = ?
            """;

        String historySql = """
            INSERT INTO workflow_history
            (order_id, previous_status, new_status, changed_by, remarks)
            VALUES (?, ?, ?, ?, ?)
            """;

        Connection connection = null;

        try {

            connection = DatabaseConnection.connectDatabase();
            connection.setAutoCommit(false);

            String currentStatus;
            int machineId;
            int changedBy;

            try (PreparedStatement selectStatement = connection.prepareStatement(selectSql)) {

                selectStatement.setInt(1, orderId);

                try (ResultSet resultSet = selectStatement.executeQuery()) {

                    if (!resultSet.next()) {
                        connection.rollback();
                        System.out.println("Invalid Order ID.");
                        return;
                    }

                    currentStatus = resultSet.getString("status");
                    machineId = resultSet.getInt("machine_id");
                    changedBy = resultSet.getInt("created_by");

                }

            }

            int rows;

            if (status.equals("IN_PROGRESS")) {

                if (!currentStatus.equals("PENDING")) {
                    connection.rollback();
                    System.out.println("Only PENDING orders can be started.");
                    return;
                }

                try (PreparedStatement machineStatement = connection.prepareStatement(busyMachineSql)) {
                    machineStatement.setInt(1, machineId);
                    if (machineStatement.executeUpdate() == 0) {
                        connection.rollback();
                        System.out.println("Assigned machine is not available.");
                        return;
                    }
                }

                try (PreparedStatement updateStatement = connection.prepareStatement(startSql)) {
                    updateStatement.setInt(1, orderId);
                    rows = updateStatement.executeUpdate();
                }

            } else if (status.equals("QUALITY_CHECK")) {

                if (!currentStatus.equals("IN_PROGRESS")) {
                    connection.rollback();
                    System.out.println("Only IN_PROGRESS orders can be sent to quality check.");
                    return;
                }

                try (PreparedStatement updateStatement = connection.prepareStatement(qualityCheckSql)) {
                    updateStatement.setInt(1, orderId);
                    rows = updateStatement.executeUpdate();
                }

                try (PreparedStatement machineStatement = connection.prepareStatement(availableMachineSql)) {
                    machineStatement.setInt(1, machineId);
                    machineStatement.executeUpdate();
                }

            } else {

                connection.rollback();
                System.out.println("Invalid workflow status.");
                return;

            }

            if (rows == 0) {
                connection.rollback();
                System.out.println("Production order status was not changed.");
                return;
            }

            try (PreparedStatement historyStatement = connection.prepareStatement(historySql)) {
                historyStatement.setInt(1, orderId);
                historyStatement.setString(2, currentStatus);
                historyStatement.setString(3, status);
                historyStatement.setInt(4, changedBy);
                historyStatement.setString(5, "Manufacturing status updated manually.");
                historyStatement.executeUpdate();
            }

            connection.commit();

            System.out.println();
            System.out.println("Production Order Status Updated.");
            System.out.println("Previous Status : " + currentStatus);
            System.out.println("New Status      : " + status);

        } catch (SQLException e) {

            try {

                if (connection != null) {
                    connection.rollback();
                }

            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }

            System.out.println("Unable to update order status.");
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
            po.priority,
            po.status,
            po.created_at
        FROM production_orders po
        INNER JOIN products p
            ON po.product_id = p.product_id
        INNER JOIN users u
            ON po.created_by = u.user_id
        LEFT JOIN machines m
            ON po.machine_id = m.machine_id
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
                System.out.println("Priority        : " + resultSet.getString("priority"));
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
