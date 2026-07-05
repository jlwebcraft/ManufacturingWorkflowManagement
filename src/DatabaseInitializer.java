import java.sql.*;

public class DatabaseInitializer {

    public static void initializeDatabase() {
        Connection dbConnection = null;

        try {

            System.out.println("Connecting to MySQL Server...");

            Connection connection = DatabaseConnection.connectServer();

            Statement statement = connection.createStatement();

            statement.executeUpdate("CREATE DATABASE IF NOT EXISTS manufacturing_workflow_db");

            System.out.println("Database Ready.");

            statement.close();
            connection.close();

            dbConnection = DatabaseConnection.connectDatabase();

            dbConnection.setAutoCommit(false);

            Statement stmt = dbConnection.createStatement();

            createAllTables(stmt);
            insertDefaultRoles(stmt);
            insertDefaultAdmin(dbConnection);

            dbConnection.commit();

            System.out.println("-----------------------------------");
            System.out.println("All tables created successfully.");
            System.out.println("-----------------------------------");

            stmt.close();
            dbConnection.close();

        } catch (SQLException e) {
            if (dbConnection != null) {
                try {
                    dbConnection.rollback();
                    System.out.println("Transaction rolled back.");
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }

            e.printStackTrace();
        }
    }

    private static void createRolesTable(Statement stmt) throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS roles(
                
                role_id INT AUTO_INCREMENT PRIMARY KEY,
                
                role_name VARCHAR(50) UNIQUE NOT NULL)
                """;

        stmt.executeUpdate(sql);

        System.out.println("roles table ready.");

    }

    private static void createUsersTable(Statement stmt) throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS users(
                
                user_id INT AUTO_INCREMENT PRIMARY KEY,
                
                role_id INT NOT NULL,
                
                name VARCHAR(100) NOT NULL,
                
                username VARCHAR(50) UNIQUE NOT NULL,
                
                email VARCHAR(100) UNIQUE NOT NULL,
                
                password_hash VARCHAR(64) NOT NULL,
                
                phone VARCHAR(15),
                
                status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
                
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                
                FOREIGN KEY(role_id)
                REFERENCES roles(role_id)
                
                )
                """;

        stmt.executeUpdate(sql);

        System.out.println("users table ready.");

    }

    private static void createProductCategoriesTable(Statement stmt) throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS product_categories(
                
                category_id INT AUTO_INCREMENT PRIMARY KEY,
                
                category_name VARCHAR(100) UNIQUE NOT NULL,
                
                description TEXT
                
                )
                """;

        stmt.executeUpdate(sql);

        System.out.println("product_categories table ready.");

    }

    private static void createProductsTable(Statement stmt) throws SQLException {

        String sql = """
                CREATE TABLE IF NOT EXISTS products(
                
                product_id INT AUTO_INCREMENT PRIMARY KEY,
                
                category_id INT NOT NULL,
                
                product_name VARCHAR(100) UNIQUE NOT NULL,
                
                description TEXT,
                
                estimated_production_hours INT NOT NULL,
                
                selling_price DECIMAL(10,2) NOT NULL,
                
                status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',
                
                FOREIGN KEY(category_id)
                REFERENCES product_categories(category_id)
                
                )
                """;

        stmt.executeUpdate(sql);

        System.out.println("products table ready.");

    }

    private static void createSuppliersTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS suppliers(

            supplier_id INT AUTO_INCREMENT PRIMARY KEY,

            supplier_name VARCHAR(100) NOT NULL,

            contact_person VARCHAR(100),

            phone VARCHAR(15),

            email VARCHAR(100),

            address TEXT,

            status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE'

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("suppliers table ready.");

    }

    private static void createRawMaterialsTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS raw_materials(

            material_id INT AUTO_INCREMENT PRIMARY KEY,

            supplier_id INT NOT NULL,

            material_name VARCHAR(100) UNIQUE NOT NULL,

            unit VARCHAR(20) NOT NULL,

            cost_per_unit DECIMAL(10,2) NOT NULL,

            status ENUM('ACTIVE','INACTIVE') DEFAULT 'ACTIVE',

            FOREIGN KEY (supplier_id)
            REFERENCES suppliers(supplier_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("raw_materials table ready.");

    }

    private static void createProductMaterialsTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS product_materials(

            bom_id INT AUTO_INCREMENT PRIMARY KEY,

            product_id INT NOT NULL,

            material_id INT NOT NULL,

            quantity_required DECIMAL(10,2) NOT NULL,

            FOREIGN KEY(product_id)
            REFERENCES products(product_id),

            FOREIGN KEY(material_id)
            REFERENCES raw_materials(material_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("product_materials table ready.");

    }

    private static void createInventoryTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS inventory(

            inventory_id INT AUTO_INCREMENT PRIMARY KEY,

            material_id INT UNIQUE NOT NULL,

            current_stock DECIMAL(10,2) DEFAULT 0,

            minimum_stock DECIMAL(10,2),

            maximum_stock DECIMAL(10,2),

            last_updated DATETIME DEFAULT CURRENT_TIMESTAMP,

            FOREIGN KEY(material_id)
            REFERENCES raw_materials(material_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("inventory table ready.");

    }

    private static void createMaterialTransactionsTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS material_transactions(

            transaction_id INT AUTO_INCREMENT PRIMARY KEY,

            material_id INT NOT NULL,

            transaction_type ENUM('IN','OUT') NOT NULL,

            quantity DECIMAL(10,2) NOT NULL,

            transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP,

            reason VARCHAR(150),

            performed_by INT,

            FOREIGN KEY(material_id)
            REFERENCES raw_materials(material_id),

            FOREIGN KEY(performed_by)
            REFERENCES users(user_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("material_transactions table ready.");

    }

    private static void createMachinesTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS machines(

            machine_id INT AUTO_INCREMENT PRIMARY KEY,

            machine_name VARCHAR(100) UNIQUE NOT NULL,

            machine_type VARCHAR(100) NOT NULL,

            daily_capacity INT NOT NULL,

            capacity_unit VARCHAR(20) NOT NULL,

            status ENUM('AVAILABLE','BUSY','MAINTENANCE','INACTIVE')
            DEFAULT 'AVAILABLE',

            purchase_date DATE,

            last_service_date DATE

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("machines table ready.");

    }

    private static void createMachineMaintenanceTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS machine_maintenance(

            maintenance_id INT AUTO_INCREMENT PRIMARY KEY,

            machine_id INT NOT NULL,

            maintenance_date DATE NOT NULL,

            maintenance_type VARCHAR(100),

            technician VARCHAR(100),

            cost DECIMAL(10,2),

            remarks TEXT,

            next_service_date DATE,

            FOREIGN KEY(machine_id)
            REFERENCES machines(machine_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("machine_maintenance table ready.");

    }

    private static void createProductionOrdersTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS production_orders(

            order_id INT AUTO_INCREMENT PRIMARY KEY,

            order_number VARCHAR(25) UNIQUE NOT NULL,

            product_id INT NOT NULL,

            machine_id INT,
            quantity INT NOT NULL,
                
           completed_quantity INT NOT NULL DEFAULT 0,
                
            priority ENUM('LOW','MEDIUM','HIGH')
            DEFAULT 'MEDIUM',

            deadline DATE NOT NULL,
                        
            production_start DATETIME,
                    
            production_end DATETIME,

            status ENUM(
            'CREATED',
            'MATERIAL_ALLOCATED',
            'IN_PRODUCTION',
            'QUALITY_CHECK',
            'PACKAGING',
            'COMPLETED',
            'CANCELLED'
            ) DEFAULT 'CREATED',

            created_by INT NOT NULL,

            created_at DATETIME DEFAULT CURRENT_TIMESTAMP,

            FOREIGN KEY(product_id)
            REFERENCES products(product_id),

            FOREIGN KEY(machine_id)
            REFERENCES machines(machine_id),

            FOREIGN KEY(created_by)
            REFERENCES users(user_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("production_orders table ready.");

    }

    private static void createProductionMaterialUsageTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS production_material_usage(

            usage_id INT AUTO_INCREMENT PRIMARY KEY,

            order_id INT NOT NULL,

            material_id INT NOT NULL,

            quantity_used DECIMAL(10,2) NOT NULL,

            recorded_at DATETIME
            DEFAULT CURRENT_TIMESTAMP,

            FOREIGN KEY(order_id)
            REFERENCES production_orders(order_id),

            FOREIGN KEY(material_id)
            REFERENCES raw_materials(material_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("production_material_usage table ready.");

    }

    private static void createWorkerAssignmentsTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS worker_assignments(

            assignment_id INT AUTO_INCREMENT PRIMARY KEY,

            order_id INT NOT NULL,

            worker_id INT NOT NULL,

            assigned_date DATE NOT NULL,

            UNIQUE(order_id, worker_id),

            FOREIGN KEY(order_id)
            REFERENCES production_orders(order_id),

            FOREIGN KEY(worker_id)
            REFERENCES users(user_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("worker_assignments table ready.");

    }

    private static void createWorkflowHistoryTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS workflow_history(

            history_id INT AUTO_INCREMENT PRIMARY KEY,

            order_id INT NOT NULL,

            previous_status VARCHAR(50),

            new_status VARCHAR(50) NOT NULL,

            changed_by INT NOT NULL,

            remarks TEXT,

            changed_at DATETIME DEFAULT CURRENT_TIMESTAMP,

            FOREIGN KEY(order_id)
            REFERENCES production_orders(order_id),

            FOREIGN KEY(changed_by)
            REFERENCES users(user_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("workflow_history table ready.");

    }

    private static void createQualityInspectionsTable(Statement stmt) throws SQLException {

        String sql = """
            CREATE TABLE IF NOT EXISTS quality_inspections(

            inspection_id INT AUTO_INCREMENT PRIMARY KEY,

            order_id INT NOT NULL,

            inspector_id INT NOT NULL,

          inspection_date DATE NOT NULL,

            result ENUM('PASS','FAIL','REWORK') NOT NULL,

            defective_quantity INT NOT NULL DEFAULT 0,

            remarks TEXT,

            FOREIGN KEY(order_id)
            REFERENCES production_orders(order_id),

            FOREIGN KEY(inspector_id)
            REFERENCES users(user_id)

            )
            """;

        stmt.executeUpdate(sql);

        System.out.println("quality_inspections table ready.");

    }

    private static void insertDefaultRoles(Statement stmt) throws SQLException {

        stmt.executeUpdate("""
            INSERT IGNORE INTO roles(role_name)
            VALUES
            ('Admin'),
            ('Production Manager'),
            ('Worker'),
            ('Quality Inspector')
            """);

        System.out.println("Default roles inserted.");

    }

    private static void insertDefaultAdmin(Connection connection) throws SQLException {

        String sql = """
            INSERT IGNORE INTO users
            (role_id, name, username, email, password_hash, phone, status)
            VALUES
            (?, ?, ?, ?, ?, ?, ?)
            """;

        PreparedStatement ps = connection.prepareStatement(sql);

        ps.setInt(1, 1); // Admin role
        ps.setString(2, "Administrator");
        ps.setString(3, "admin");
        ps.setString(4, "admin@mwms.com");
        ps.setString(5, PasswordHasher.hashPassword("admin123"));
        ps.setString(6, "9999999999");
        ps.setString(7, "ACTIVE");

        ps.executeUpdate();

        ps.close();

        System.out.println("Default admin account created.");

    }

    private static void createAllTables(Statement stmt) throws SQLException {

        createRolesTable(stmt);
        createUsersTable(stmt);

        createProductCategoriesTable(stmt);
        createProductsTable(stmt);

        createSuppliersTable(stmt);
        createRawMaterialsTable(stmt);
        createProductMaterialsTable(stmt);
        createInventoryTable(stmt);
        createMaterialTransactionsTable(stmt);

        createMachinesTable(stmt);
        createMachineMaintenanceTable(stmt);
        createProductionOrdersTable(stmt);
        createProductionMaterialUsageTable(stmt);

        createWorkerAssignmentsTable(stmt);
        createWorkflowHistoryTable(stmt);
        createQualityInspectionsTable(stmt);
    }
}
