package database;

import util.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSeeder {

    public static void seedAll(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            seedRoles(statement);
            seedUsers(connection);
            seedProductCategories(statement);
            seedProducts(statement);
            seedRawMaterials(statement);
            seedProductMaterials(statement);
            seedInventory(statement);
            seedMaterialTransactions(statement);
            seedMachines(statement);
            seedProductionOrders(statement);
            seedProductionMaterialUsage(statement);
            seedWorkerAssignments(statement);
            seedWorkflowHistory(statement);
            seedQualityInspections(statement);
            seedFinishedGoodsInventory(statement);

        }

        System.out.println("Sample PC manufacturing data inserted.");

    }

    private static void seedRoles(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO roles (role_id, role_name)
                VALUES
                    (1, 'Admin'),
                    (2, 'Production Manager'),
                    (3, 'Worker'),
                    (4, 'Quality Inspector')
                """);

    }

    private static void seedUsers(Connection connection) throws SQLException {

        String sql = """
                INSERT IGNORE INTO users
                    (user_id, role_id, name, username, email, password_hash, phone, status)
                VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            addUser(preparedStatement, 1, 1, "Administrator", "admin",
                    "admin@mwms.com", "admin123", "9999999999");
            addUser(preparedStatement, 2, 2, "Ravi Production Manager", "ravi.pm",
                    "ravi.pm@mwms.com", "manager123", "9876543210");
            addUser(preparedStatement, 3, 3, "Asha Assembly Worker", "asha.worker",
                    "asha.worker@mwms.com", "worker123", "9876500011");
            addUser(preparedStatement, 4, 3, "Imran Testing Worker", "imran.worker",
                    "imran.worker@mwms.com", "worker123", "9876500012");
            addUser(preparedStatement, 5, 4, "Neha Quality Inspector", "neha.qc",
                    "neha.qc@mwms.com", "quality123", "9876500013");

        }

    }

    private static void addUser(PreparedStatement preparedStatement,
                                int userId,
                                int roleId,
                                String name,
                                String username,
                                String email,
                                String password,
                                String phone) throws SQLException {

        preparedStatement.setInt(1, userId);
        preparedStatement.setInt(2, roleId);
        preparedStatement.setString(3, name);
        preparedStatement.setString(4, username);
        preparedStatement.setString(5, email);
        preparedStatement.setString(6, PasswordHasher.hashPassword(password));
        preparedStatement.setString(7, phone);
        preparedStatement.setString(8, "ACTIVE");
        preparedStatement.executeUpdate();

    }

    private static void seedProductCategories(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO product_categories
                    (category_id, category_name, description)
                VALUES
                    (1, 'Desktop Computers', 'Complete desktop PC systems.'),
                    (2, 'Workstations', 'High performance PCs for professional workloads.'),
                    (3, 'Gaming Computers', 'Performance PCs for gaming and streaming.')
                """);

    }

    private static void seedProducts(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO products
                    (product_id, category_id, product_name, description,
                     estimated_production_hours, selling_price, status)
                VALUES
                    (1, 1, 'Office Desktop PC',
                     'Reliable desktop system for office productivity.',
                     6, 45000.00, 'ACTIVE'),
                    (2, 2, 'Design Workstation PC',
                     'High memory workstation for design and engineering software.',
                     10, 125000.00, 'ACTIVE'),
                    (3, 3, 'Gaming Performance PC',
                     'RGB gaming desktop with dedicated graphics card.',
                     8, 95000.00, 'ACTIVE')
                """);

    }

    private static void seedRawMaterials(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO raw_materials
                    (material_id, material_name, unit, cost_per_unit, status)
                VALUES
                    (1, 'CPU', 'Piece', 12500.00, 'ACTIVE'),
                    (2, 'Motherboard', 'Piece', 8500.00, 'ACTIVE'),
                    (3, 'RAM 16GB', 'Piece', 4200.00, 'ACTIVE'),
                    (4, 'SSD 512GB', 'Piece', 3600.00, 'ACTIVE'),
                    (5, 'Power Supply Unit', 'Piece', 3200.00, 'ACTIVE'),
                    (6, 'PC Cabinet', 'Piece', 2800.00, 'ACTIVE'),
                    (7, 'Graphics Card', 'Piece', 28000.00, 'ACTIVE'),
                    (8, 'Cooling Fan', 'Piece', 550.00, 'ACTIVE')
                """);

    }

    private static void seedProductMaterials(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO product_materials
                    (bom_id, product_id, material_id, quantity_required)
                VALUES
                    (1, 1, 1, 1.00),
                    (2, 1, 2, 1.00),
                    (3, 1, 3, 1.00),
                    (4, 1, 4, 1.00),
                    (5, 1, 5, 1.00),
                    (6, 1, 6, 1.00),
                    (7, 2, 1, 1.00),
                    (8, 2, 2, 1.00),
                    (9, 2, 3, 2.00),
                    (10, 2, 4, 2.00),
                    (11, 2, 5, 1.00),
                    (12, 2, 6, 1.00),
                    (13, 2, 8, 2.00),
                    (14, 3, 1, 1.00),
                    (15, 3, 2, 1.00),
                    (16, 3, 3, 2.00),
                    (17, 3, 4, 1.00),
                    (18, 3, 5, 1.00),
                    (19, 3, 6, 1.00),
                    (20, 3, 7, 1.00),
                    (21, 3, 8, 3.00)
                """);

    }

    private static void seedInventory(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO inventory
                    (inventory_id, material_id, inventory_name, current_stock, minimum_stock, maximum_stock)
                VALUES
                    (1, 1, 'CPU Stock', 80.00, 20.00, 150.00),
                    (2, 2, 'Motherboard Stock', 75.00, 20.00, 150.00),
                    (3, 3, 'RAM Stock', 160.00, 40.00, 300.00),
                    (4, 4, 'SSD Stock', 140.00, 35.00, 250.00),
                    (5, 5, 'Power Supply Stock', 90.00, 20.00, 150.00),
                    (6, 6, 'Cabinet Stock', 95.00, 20.00, 150.00),
                    (7, 7, 'Graphics Card Stock', 45.00, 10.00, 80.00),
                    (8, 8, 'Cooling Fan Stock', 220.00, 50.00, 400.00)
                """);

    }

    private static void seedMaterialTransactions(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO material_transactions
                    (transaction_id, material_id, transaction_type, quantity,
                     transaction_date, reason, performed_by)
                VALUES
                    (1, 1, 'IN', 100.00, '2026-08-01 09:00:00', 'Initial stock from Buy Department', 2),
                    (2, 2, 'IN', 100.00, '2026-08-01 09:10:00', 'Initial stock from Buy Department', 2),
                    (3, 3, 'IN', 200.00, '2026-08-01 09:20:00', 'Initial stock from Buy Department', 2),
                    (4, 4, 'IN', 180.00, '2026-08-01 09:30:00', 'Initial stock from Buy Department', 2),
                    (5, 7, 'IN', 60.00, '2026-08-01 09:40:00', 'Initial stock from Buy Department', 2),
                    (6, 1, 'OUT', 20.00, '2026-08-03 11:00:00', 'Production Order', 2),
                    (7, 2, 'OUT', 20.00, '2026-08-03 11:00:00', 'Production Order', 2),
                    (8, 3, 'OUT', 40.00, '2026-08-03 11:00:00', 'Production Order', 2)
                """);

    }


    private static void seedMachines(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO machines
                    (machine_id, machine_name, machine_type, daily_capacity, status)
                VALUES
                    (1, 'Assembly Line A', 'PC Assembly', 25, 'AVAILABLE'),
                    (2, 'Testing Bench B', 'Hardware Testing', 30, 'AVAILABLE'),
                    (3, 'Burn-In Rack C', 'Stress Testing', 20, 'AVAILABLE')
                """);

        statement.executeUpdate("""
                UPDATE machines
                SET status = 'AVAILABLE'
                WHERE machine_id IN (1, 2, 3)
                """);

    }
    private static void seedProductionOrders(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO production_orders
                    (order_id, order_number, product_id, machine_id, quantity,
                     completed_quantity, priority, deadline,
                     production_start, production_end, status, created_by, created_at)
                VALUES
                    (1, 'PO-20260803-001', 1, 1, 20, 20, 'MEDIUM',
                     '2026-08-10', '2026-08-03 11:00:00', '2026-08-03 17:00:00',
                     'COMPLETED', 2, '2026-08-03 10:30:00'),
                    (2, 'PO-20260804-002', 3, 2, 10, 0, 'HIGH',
                     '2026-08-12', NULL, NULL, 'PENDING', 2, '2026-08-04 14:00:00'),
                    (3, 'PO-20260805-003', 2, 3, 5, 5, 'HIGH',
                     '2026-08-15', '2026-08-05 09:00:00', '2026-08-05 14:00:00',
                     'QUALITY_CHECK', 2, '2026-08-05 08:30:00')
                """);

        statement.executeUpdate("""
                UPDATE production_orders
                SET product_id = 1,
                    machine_id = 1,
                    quantity = 20,
                    completed_quantity = 20,
                    priority = 'MEDIUM',
                    deadline = '2026-08-10',
                    production_start = '2026-08-03 11:00:00',
                    production_end = '2026-08-03 17:00:00',
                    status = 'COMPLETED',
                    created_by = 2
                WHERE order_number = 'PO-20260803-001'
                """);

        statement.executeUpdate("""
                UPDATE production_orders
                SET product_id = 3,
                    machine_id = 2,
                    quantity = 10,
                    completed_quantity = 0,
                    priority = 'HIGH',
                    deadline = '2026-08-12',
                    production_start = NULL,
                    production_end = NULL,
                    status = 'PENDING',
                    created_by = 2
                WHERE order_number = 'PO-20260804-002'
                """);

        statement.executeUpdate("""
                UPDATE production_orders
                SET product_id = 2,
                    machine_id = 3,
                    quantity = 5,
                    completed_quantity = 5,
                    priority = 'HIGH',
                    deadline = '2026-08-15',
                    production_start = '2026-08-05 09:00:00',
                    production_end = '2026-08-05 14:00:00',
                    status = 'QUALITY_CHECK',
                    created_by = 2
                WHERE order_number = 'PO-20260805-003'
                """);

        statement.executeUpdate("""
                UPDATE production_orders
                SET machine_id = CASE MOD(order_id - 1, 3)
                    WHEN 0 THEN 1
                    WHEN 1 THEN 2
                    ELSE 3
                END
                WHERE machine_id IS NULL
                """);

    }

    private static void seedProductionMaterialUsage(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO production_material_usage
                    (usage_id, order_id, material_id, quantity_used, recorded_at)
                VALUES
                    (1, 1, 1, 20.00, '2026-08-03 11:05:00'),
                    (2, 1, 2, 20.00, '2026-08-03 11:05:00'),
                    (3, 1, 3, 20.00, '2026-08-03 11:05:00'),
                    (4, 1, 4, 20.00, '2026-08-03 11:05:00'),
                    (5, 1, 5, 20.00, '2026-08-03 11:05:00'),
                    (6, 1, 6, 20.00, '2026-08-03 11:05:00')
                """);

    }

    private static void seedWorkerAssignments(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO worker_assignments
                    (assignment_id, order_id, worker_id, assigned_date)
                VALUES
                    (1, 1, 3, '2026-08-03'),
                    (2, 1, 4, '2026-08-03'),
                    (3, 3, 3, '2026-08-05')
                """);

    }

    private static void seedWorkflowHistory(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO workflow_history
                    (history_id, order_id, previous_status, new_status,
                     changed_by, remarks, changed_at)
                VALUES
                    (1, 1, NULL, 'PENDING', 2, 'Production order created.', '2026-08-03 10:30:00'),
                    (2, 1, 'PENDING', 'IN_PROGRESS', 2, 'Manufacturing started.', '2026-08-03 11:00:00'),
                    (3, 1, 'IN_PROGRESS', 'COMPLETED', 2, 'Office desktops completed.', '2026-08-03 17:00:00'),
                    (4, 2, NULL, 'PENDING', 2, 'Gaming PC order queued.', '2026-08-04 14:00:00'),
                    (5, 3, 'PENDING', 'IN_PROGRESS', 2, 'Workstation build started.', '2026-08-05 09:00:00'),
                    (6, 3, 'IN_PROGRESS', 'QUALITY_CHECK', 2, 'Workstation build completed and sent for inspection.', '2026-08-05 14:00:00')
                """);

    }


    private static void seedFinishedGoodsInventory(Statement statement) throws SQLException {

        statement.executeUpdate("""
                INSERT IGNORE INTO finished_goods_inventory
                    (finished_goods_id, product_id, available_quantity)
                VALUES
                    (1, 1, 20),
                    (2, 2, 0),
                    (3, 3, 0)
                """);

        statement.executeUpdate("""
                UPDATE finished_goods_inventory
                SET available_quantity = CASE product_id
                    WHEN 1 THEN 20
                    WHEN 2 THEN 0
                    WHEN 3 THEN 0
                    ELSE available_quantity
                END
                WHERE product_id IN (1, 2, 3)
                """);

    }
    private static void seedQualityInspections(Statement statement) throws SQLException {

        statement.executeUpdate("""
                DELETE FROM quality_inspections
                WHERE order_id IN (2, 3)
                """);

        statement.executeUpdate("""
                INSERT IGNORE INTO quality_inspections
                    (inspection_id, order_id, inspector_id, inspection_date,
                     result, defective_quantity, remarks)
                VALUES
                    (1, 1, 5, '2026-08-04', 'PASS', 0,
                     'All office desktop PCs passed boot and hardware diagnostics.')
                """);

    }

}

