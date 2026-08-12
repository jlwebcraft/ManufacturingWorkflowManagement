package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    public static boolean initializeDatabase() {

        try {

            System.out.println("Connecting to MySQL Server...");

            try (
                    Connection serverConnection = DatabaseConnection.connectServer();
                    Statement serverStatement = serverConnection.createStatement()
            ) {

                serverStatement.executeUpdate("CREATE DATABASE IF NOT EXISTS manufacturing_workflow_db");

            }

            System.out.println("Database Ready.");

            try (Connection dbConnection = DatabaseConnection.connectDatabase()) {

                dbConnection.setAutoCommit(false);

                try (Statement statement = dbConnection.createStatement()) {

                    createAllTables(statement);
                    repairExistingSchema(dbConnection, statement);
                    createDatabaseObjects(statement);
                    DatabaseSeeder.seedAll(dbConnection);

                }

                dbConnection.commit();

            } catch (SQLException e) {

                System.out.println("Unable to initialize database.");
                throw e;

            }

            System.out.println("-----------------------------------");
            System.out.println("All tables created and seeded successfully.");
            System.out.println("-----------------------------------");
            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            return false;

        }

    }

    private static void createRolesTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS roles (
                    role_id INT AUTO_INCREMENT PRIMARY KEY,
                    role_name VARCHAR(50) UNIQUE NOT NULL
                )
                """);
        System.out.println("roles table ready.");
    }

    private static void createUsersTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    role_id INT NOT NULL,
                    name VARCHAR(100) NOT NULL,
                    username VARCHAR(50) UNIQUE NOT NULL,
                    email VARCHAR(100) UNIQUE NOT NULL,
                    password_hash VARCHAR(64) NOT NULL,
                    phone VARCHAR(15),
                    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (role_id) REFERENCES roles(role_id)
                )
                """);
        System.out.println("users table ready.");
    }

    private static void createProductCategoriesTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS product_categories (
                    category_id INT AUTO_INCREMENT PRIMARY KEY,
                    category_name VARCHAR(100) UNIQUE NOT NULL,
                    description TEXT
                )
                """);
        System.out.println("product_categories table ready.");
    }

    private static void createProductsTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS products (
                    product_id INT AUTO_INCREMENT PRIMARY KEY,
                    category_id INT NOT NULL,
                    product_name VARCHAR(100) UNIQUE NOT NULL,
                    description TEXT,
                    estimated_production_hours INT NOT NULL CHECK (estimated_production_hours > 0),
                    selling_price DECIMAL(10,2) NOT NULL CHECK (selling_price >= 0),
                    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE',
                    FOREIGN KEY (category_id) REFERENCES product_categories(category_id)
                )
                """);
        System.out.println("products table ready.");
    }

    private static void createRawMaterialsTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS raw_materials (
                    material_id INT AUTO_INCREMENT PRIMARY KEY,
                    material_name VARCHAR(100) UNIQUE NOT NULL,
                    unit VARCHAR(20) NOT NULL,
                    cost_per_unit DECIMAL(10,2) NOT NULL CHECK (cost_per_unit >= 0),
                    status ENUM('ACTIVE', 'INACTIVE') DEFAULT 'ACTIVE'
                )
                """);
        System.out.println("raw_materials table ready.");
    }

    private static void createProductMaterialsTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS product_materials (
                    bom_id INT AUTO_INCREMENT PRIMARY KEY,
                    product_id INT NOT NULL,
                    material_id INT NOT NULL,
                    quantity_required DECIMAL(10,2) NOT NULL CHECK (quantity_required > 0),
                    UNIQUE (product_id, material_id),
                    FOREIGN KEY (product_id) REFERENCES products(product_id),
                    FOREIGN KEY (material_id) REFERENCES raw_materials(material_id)
                )
                """);
        System.out.println("product_materials table ready.");
    }

    private static void createInventoryTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS inventory (
                    inventory_id INT AUTO_INCREMENT PRIMARY KEY,
                    inventory_name VARCHAR(100) NOT NULL,
                    material_id INT UNIQUE NOT NULL,
                    current_stock DECIMAL(10,2) DEFAULT 0 CHECK (current_stock >= 0),
                    minimum_stock DECIMAL(10,2) CHECK (minimum_stock >= 0),
                    maximum_stock DECIMAL(10,2) CHECK (maximum_stock >= 0),
                    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (material_id) REFERENCES raw_materials(material_id)
                )
                """);
        System.out.println("inventory table ready.");
    }

    private static void createMaterialTransactionsTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS material_transactions (
                    transaction_id INT AUTO_INCREMENT PRIMARY KEY,
                    material_id INT NOT NULL,
                    transaction_type ENUM('IN', 'OUT') NOT NULL,
                    quantity DECIMAL(10,2) NOT NULL CHECK (quantity > 0),
                    transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    reason VARCHAR(150),
                    performed_by INT,
                    FOREIGN KEY (material_id) REFERENCES raw_materials(material_id),
                    FOREIGN KEY (performed_by) REFERENCES users(user_id)
                )
                """);
        System.out.println("material_transactions table ready.");
    }

    private static void createMachinesTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS machines (
                    machine_id INT AUTO_INCREMENT PRIMARY KEY,
                    machine_name VARCHAR(100) UNIQUE NOT NULL,
                    machine_type VARCHAR(100) NOT NULL,
                    daily_capacity INT NOT NULL CHECK (daily_capacity > 0),
                    status ENUM('AVAILABLE', 'BUSY', 'MAINTENANCE', 'INACTIVE') DEFAULT 'AVAILABLE'
                )
                """);
        System.out.println("machines table ready.");
    }

    private static void createProductionOrdersTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS production_orders (
                    order_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_number VARCHAR(25) UNIQUE NOT NULL,
                    product_id INT NOT NULL,
                    machine_id INT,
                    quantity INT NOT NULL CHECK (quantity > 0),
                    completed_quantity INT NOT NULL DEFAULT 0 CHECK (completed_quantity >= 0),
                    priority ENUM('LOW', 'MEDIUM', 'HIGH') DEFAULT 'MEDIUM',
                    deadline DATE,
                    production_start DATETIME,
                    production_end DATETIME,
                    status ENUM('PENDING', 'IN_PROGRESS', 'CREATED', 'MATERIAL_ALLOCATED', 'IN_PRODUCTION', 'QUALITY_CHECK', 'PACKAGING', 'COMPLETED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING',
                    created_by INT NOT NULL,
                    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (product_id) REFERENCES products(product_id),
                    FOREIGN KEY (machine_id) REFERENCES machines(machine_id),
                    FOREIGN KEY (created_by) REFERENCES users(user_id)
                )
                """);
        System.out.println("production_orders table ready.");
    }

    private static void createProductionMaterialUsageTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS production_material_usage (
                    usage_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    material_id INT NOT NULL,
                    quantity_used DECIMAL(10,2) NOT NULL CHECK (quantity_used > 0),
                    recorded_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (order_id) REFERENCES production_orders(order_id),
                    FOREIGN KEY (material_id) REFERENCES raw_materials(material_id)
                )
                """);
        System.out.println("production_material_usage table ready.");
    }

    private static void createWorkerAssignmentsTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS worker_assignments (
                    assignment_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    worker_id INT NOT NULL,
                    assigned_date DATE NOT NULL,
                    UNIQUE (order_id, worker_id),
                    FOREIGN KEY (order_id) REFERENCES production_orders(order_id),
                    FOREIGN KEY (worker_id) REFERENCES users(user_id)
                )
                """);
        System.out.println("worker_assignments table ready.");
    }

    private static void createWorkflowHistoryTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS workflow_history (
                    history_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    previous_status VARCHAR(50),
                    new_status VARCHAR(50) NOT NULL,
                    changed_by INT NOT NULL,
                    remarks TEXT,
                    changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (order_id) REFERENCES production_orders(order_id),
                    FOREIGN KEY (changed_by) REFERENCES users(user_id)
                )
                """);
        System.out.println("workflow_history table ready.");
    }

    private static void createQualityInspectionsTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS quality_inspections (
                    inspection_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT NOT NULL,
                    inspector_id INT NOT NULL,
                    inspection_date DATE NOT NULL,
                    result ENUM('PASS', 'FAIL', 'REWORK') NOT NULL,
                    defective_quantity INT NOT NULL DEFAULT 0 CHECK (defective_quantity >= 0),
                    remarks TEXT,
                    FOREIGN KEY (order_id) REFERENCES production_orders(order_id),
                    FOREIGN KEY (inspector_id) REFERENCES users(user_id)
                )
                """);
        System.out.println("quality_inspections table ready.");
    }

    private static void createFinishedGoodsInventoryTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS finished_goods_inventory (
                    finished_goods_id INT AUTO_INCREMENT PRIMARY KEY,
                    product_id INT UNIQUE NOT NULL,
                    available_quantity INT NOT NULL DEFAULT 0 CHECK (available_quantity >= 0),
                    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (product_id) REFERENCES products(product_id)
                )
                """);
        System.out.println("finished_goods_inventory table ready.");
    }

    private static void createDeliveriesTable(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS deliveries (
                    delivery_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT UNIQUE NOT NULL,
                    delivered_by INT NOT NULL,
                    delivered_quantity INT NOT NULL CHECK (delivered_quantity > 0),
                    delivered_to VARCHAR(100) NOT NULL,
                    delivery_address VARCHAR(255),
                    delivery_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    remarks TEXT,
                    FOREIGN KEY (order_id) REFERENCES production_orders(order_id),
                    FOREIGN KEY (delivered_by) REFERENCES users(user_id)
                )
                """);
        System.out.println("deliveries table ready.");
    }

    private static void repairExistingSchema(Connection connection, Statement statement) throws SQLException {
        ensureInventoryNameColumn(connection, statement);
        ensureMachineSchema(connection, statement);
        ensureFinishedGoodsSchema(statement);
        ensureDeliveriesSchema(statement);
        ensureProductionOrderStatusValues(statement);
    }

    private static void ensureInventoryNameColumn(Connection connection, Statement statement) throws SQLException {
        if (columnExists(connection, "inventory", "inventory_name")) {
            return;
        }

        statement.executeUpdate("""
                ALTER TABLE inventory
                ADD COLUMN inventory_name VARCHAR(100) NULL AFTER inventory_id
                """);
        statement.executeUpdate("""
                UPDATE inventory i
                INNER JOIN raw_materials rm ON i.material_id = rm.material_id
                SET i.inventory_name = CONCAT(rm.material_name, ' Stock')
                WHERE i.inventory_name IS NULL OR i.inventory_name = ''
                """);
        statement.executeUpdate("""
                UPDATE inventory
                SET inventory_name = CONCAT('Inventory ', inventory_id)
                WHERE inventory_name IS NULL OR inventory_name = ''
                """);
        statement.executeUpdate("""
                ALTER TABLE inventory
                MODIFY inventory_name VARCHAR(100) NOT NULL
                """);
        System.out.println("inventory_name column added to existing inventory table.");
    }

    private static void ensureMachineSchema(Connection connection, Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS machines (
                    machine_id INT AUTO_INCREMENT PRIMARY KEY,
                    machine_name VARCHAR(100) UNIQUE NOT NULL,
                    machine_type VARCHAR(100) NOT NULL,
                    daily_capacity INT NOT NULL CHECK (daily_capacity > 0),
                    status ENUM('AVAILABLE', 'BUSY', 'MAINTENANCE', 'INACTIVE') DEFAULT 'AVAILABLE'
                )
                """);

        if (!columnExists(connection, "production_orders", "machine_id")) {
            statement.executeUpdate("""
                    ALTER TABLE production_orders
                    ADD COLUMN machine_id INT NULL AFTER product_id
                    """);
        }
    }

    private static void ensureFinishedGoodsSchema(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS finished_goods_inventory (
                    finished_goods_id INT AUTO_INCREMENT PRIMARY KEY,
                    product_id INT UNIQUE NOT NULL,
                    available_quantity INT NOT NULL DEFAULT 0 CHECK (available_quantity >= 0),
                    last_updated DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (product_id) REFERENCES products(product_id)
                )
                """);
    }

    private static void ensureDeliveriesSchema(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE TABLE IF NOT EXISTS deliveries (
                    delivery_id INT AUTO_INCREMENT PRIMARY KEY,
                    order_id INT UNIQUE NOT NULL,
                    delivered_by INT NOT NULL,
                    delivered_quantity INT NOT NULL CHECK (delivered_quantity > 0),
                    delivered_to VARCHAR(100) NOT NULL,
                    delivery_address VARCHAR(255),
                    delivery_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                    remarks TEXT,
                    FOREIGN KEY (order_id) REFERENCES production_orders(order_id),
                    FOREIGN KEY (delivered_by) REFERENCES users(user_id)
                )
                """);
    }

    private static void ensureProductionOrderStatusValues(Statement statement) throws SQLException {
        statement.executeUpdate("""
                ALTER TABLE production_orders
                MODIFY status ENUM('PENDING', 'IN_PROGRESS', 'CREATED', 'MATERIAL_ALLOCATED', 'IN_PRODUCTION', 'QUALITY_CHECK', 'PACKAGING', 'COMPLETED', 'DELIVERED', 'CANCELLED') DEFAULT 'PENDING'
                """);
    }

    private static void createDatabaseObjects(Statement statement) throws SQLException {
        statement.executeUpdate("""
                CREATE OR REPLACE VIEW vw_inventory_summary AS
                SELECT rm.material_name,
                       i.current_stock,
                       i.minimum_stock,
                       i.maximum_stock,
                       rm.unit
                FROM inventory i
                INNER JOIN raw_materials rm ON i.material_id = rm.material_id
                """);

        statement.executeUpdate("""
                CREATE OR REPLACE VIEW vw_production_summary AS
                SELECT p.product_name,
                       COUNT(po.order_id) AS total_orders,
                       COALESCE(SUM(po.quantity), 0) AS planned_quantity,
                       COALESCE(SUM(po.completed_quantity), 0) AS completed_quantity
                FROM products p
                LEFT JOIN production_orders po ON p.product_id = po.product_id
                GROUP BY p.product_id, p.product_name
                """);

        statement.executeUpdate("DROP PROCEDURE IF EXISTS sp_record_material_transaction");
        statement.executeUpdate("""
                CREATE PROCEDURE sp_record_material_transaction(
                    IN p_material_id INT,
                    IN p_transaction_type VARCHAR(10),
                    IN p_quantity DECIMAL(10,2),
                    IN p_reason VARCHAR(150),
                    IN p_performed_by INT
                )
                BEGIN
                    INSERT INTO material_transactions
                    (material_id, transaction_type, quantity, reason, performed_by)
                    VALUES (p_material_id, p_transaction_type, p_quantity, p_reason, p_performed_by);
                END
                """);

        statement.executeUpdate("DROP FUNCTION IF EXISTS fn_available_stock");
        statement.executeUpdate("""
                CREATE FUNCTION fn_available_stock(p_material_id INT)
                RETURNS DECIMAL(10,2)
                DETERMINISTIC
                READS SQL DATA
                BEGIN
                    DECLARE stock DECIMAL(10,2);
                    SELECT COALESCE(current_stock, 0)
                    INTO stock
                    FROM inventory
                    WHERE material_id = p_material_id;
                    RETURN COALESCE(stock, 0);
                END
                """);

        statement.executeUpdate("DROP TRIGGER IF EXISTS trg_inventory_no_negative_stock");
        statement.executeUpdate("""
                CREATE TRIGGER trg_inventory_no_negative_stock
                BEFORE UPDATE ON inventory
                FOR EACH ROW
                BEGIN
                    IF NEW.current_stock < 0 THEN
                        SIGNAL SQLSTATE '45000'
                        SET MESSAGE_TEXT = 'Inventory stock cannot be negative';
                    END IF;
                END
                """);
    }

    private static boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        String sql = """
                SELECT COUNT(*)
                FROM INFORMATION_SCHEMA.COLUMNS
                WHERE TABLE_SCHEMA = DATABASE()
                  AND TABLE_NAME = ?
                  AND COLUMN_NAME = ?
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, tableName);
            preparedStatement.setString(2, columnName);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next() && resultSet.getInt(1) > 0;
            }
        }
    }

    private static void createAllTables(Statement statement) throws SQLException {
        createRolesTable(statement);
        createUsersTable(statement);
        createProductCategoriesTable(statement);
        createProductsTable(statement);
        createRawMaterialsTable(statement);
        createProductMaterialsTable(statement);
        createInventoryTable(statement);
        createMaterialTransactionsTable(statement);
        createMachinesTable(statement);
        createProductionOrdersTable(statement);
        createProductionMaterialUsageTable(statement);
        createWorkerAssignmentsTable(statement);
        createWorkflowHistoryTable(statement);
        createQualityInspectionsTable(statement);
        createFinishedGoodsInventoryTable(statement);
        createDeliveriesTable(statement);
    }
}
