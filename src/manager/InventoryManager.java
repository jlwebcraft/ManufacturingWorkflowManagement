package manager;

import database.DatabaseConnection;
import model.Inventory;
import util.ConsoleFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class InventoryManager {

    public static void addInventoryRecord(Inventory inventory) {

        String sql = """
                INSERT INTO inventory
                (material_id, inventory_name, current_stock, minimum_stock, maximum_stock)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
        ) {

            preparedStatement.setInt(1, inventory.getMaterialId());
            preparedStatement.setString(2, inventory.getInventoryName());
            preparedStatement.setDouble(3, inventory.getCurrentStock());
            preparedStatement.setDouble(4, inventory.getMinimumStock());
            preparedStatement.setDouble(5, inventory.getMaximumStock());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {

                    if (generatedKeys.next()) {
                        inventory.setInventoryId(generatedKeys.getInt(1));
                    }

                }

                System.out.println("Inventory record created successfully.");
                System.out.println("Generated Inventory ID : " + inventory.getInventoryId());

            }

        } catch (SQLException e) {

            System.out.println("Unable to create inventory record.");
            e.printStackTrace();

        }

    }

    public static void viewInventory() {

        String sql = """
            SELECT i.inventory_id,
                   i.inventory_name,
                   rm.material_name,
                   i.current_stock,
                   i.minimum_stock,
                   i.maximum_stock
            FROM inventory i
            INNER JOIN raw_materials rm
                ON i.material_id = rm.material_id
            ORDER BY i.inventory_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            boolean found = false;

            System.out.println();
            System.out.println("==================================================");
            System.out.println("\t\tCURRENT INVENTORY");
            System.out.println("==================================================");
            System.out.println();
            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Inventory Name", 28)
                            + ConsoleFormatter.padRight("Raw Material", 25)
                            + ConsoleFormatter.padRight("Current", 12)
                            + ConsoleFormatter.padRight("Minimum", 12)
                            + "Maximum"
            );
            System.out.println("----------------------------------------------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("inventory_id")), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("inventory_name"), 28)
                                + ConsoleFormatter.padRight(resultSet.getString("material_name"), 25)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getDouble("current_stock")), 12)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getDouble("minimum_stock")), 12)
                                + resultSet.getDouble("maximum_stock")
                );

            }

            if (!found) {
                System.out.println("No inventory records found.");
            }

        } catch (SQLException e) {

            System.out.println("Unable to view inventory.");
            e.printStackTrace();

        }

    }

    public static boolean inventoryExists(int inventoryId) {

        String sql = """
            SELECT inventory_id
            FROM inventory
            WHERE inventory_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, inventoryId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void addStock(int inventoryId, double quantity) {

        String sql = """
            UPDATE inventory
            SET current_stock = current_stock + ?
            WHERE inventory_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, inventoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Stock added successfully.");
            }

        } catch (SQLException e) {

            System.out.println("Unable to add stock.");
            e.printStackTrace();

        }

    }

    public static void removeStock(int inventoryId, double quantity) {

        String sql = """
            UPDATE inventory
            SET current_stock = current_stock - ?
            WHERE inventory_id = ?
            AND current_stock >= ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, inventoryId);
            preparedStatement.setDouble(3, quantity);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Stock removed successfully.");
            } else {
                System.out.println("Insufficient stock.");
            }

        } catch (SQLException e) {

            System.out.println("Unable to remove stock.");
            e.printStackTrace();

        }

    }

    public static void removeRawMaterialFromInventory(int inventoryId) {

        String sql = """
            DELETE FROM inventory
            WHERE inventory_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, inventoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Raw material removed from inventory successfully.");
            } else {
                System.out.println("Invalid Inventory ID.");
            }

        } catch (SQLException e) {

            System.out.println("Unable to remove raw material from inventory.");
            e.printStackTrace();

        }

    }

    public static void searchInventory(String keyword) {

        String sql = """
            SELECT i.inventory_id,
                   i.inventory_name,
                   rm.material_name,
                   i.current_stock,
                   i.minimum_stock,
                   i.maximum_stock
            FROM inventory i
            INNER JOIN raw_materials rm
                ON i.material_id = rm.material_id
            WHERE i.inventory_name LIKE ?
               OR rm.material_name LIKE ?
            ORDER BY i.inventory_name
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, "%" + keyword + "%");
            preparedStatement.setString(2, "%" + keyword + "%");

            try (ResultSet resultSet = preparedStatement.executeQuery()) {

                boolean found = false;

                System.out.println();
                System.out.println("==================================================");
                System.out.println("\t\tSEARCH RESULTS");
                System.out.println("==================================================");
                System.out.println();

                while (resultSet.next()) {

                    found = true;

                    System.out.println("--------------------------------------------------");
                    System.out.println("Inventory ID   : " + resultSet.getInt("inventory_id"));
                    System.out.println("Inventory Name : " + resultSet.getString("inventory_name"));
                    System.out.println("Material       : " + resultSet.getString("material_name"));
                    System.out.println("Current Stock  : " + resultSet.getDouble("current_stock"));
                    System.out.println("Minimum Stock  : " + resultSet.getDouble("minimum_stock"));
                    System.out.println("Maximum Stock  : " + resultSet.getDouble("maximum_stock"));
                    System.out.println("--------------------------------------------------");

                }

                if (!found) {
                    System.out.println("No matching inventory found.");
                }

            }

        } catch (SQLException e) {

            System.out.println("Unable to search inventory.");
            e.printStackTrace();

        }

    }

    public static boolean inventoryRecordExists(int materialId) {

        String sql = """
            SELECT inventory_id
            FROM inventory
            WHERE material_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, materialId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void orderRawMaterial(int materialId, double quantity) {

        String inventorySql = """
            INSERT INTO inventory
            (material_id, inventory_name, current_stock, minimum_stock, maximum_stock)
            SELECT material_id, material_name, ?, 0, ?
            FROM raw_materials
            WHERE material_id = ?
            ON DUPLICATE KEY UPDATE
                current_stock = current_stock + VALUES(current_stock),
                maximum_stock = GREATEST(maximum_stock, current_stock + VALUES(current_stock))
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

        Connection connection = null;

        try {

            connection = DatabaseConnection.connectDatabase();
            connection.setAutoCommit(false);

            try (
                    PreparedStatement inventoryStatement = connection.prepareStatement(inventorySql);
                    PreparedStatement transactionStatement = connection.prepareStatement(transactionSql)
            ) {

                inventoryStatement.setDouble(1, quantity);
                inventoryStatement.setDouble(2, quantity);
                inventoryStatement.setInt(3, materialId);
                inventoryStatement.executeUpdate();

                transactionStatement.setInt(1, materialId);
                transactionStatement.setString(2, "IN");
                transactionStatement.setDouble(3, quantity);
                transactionStatement.setString(4, "Buy Department Raw Material Order");
                transactionStatement.setNull(5, Types.INTEGER);
                transactionStatement.executeUpdate();

                connection.commit();

                System.out.println();
                System.out.println("Raw material order placed successfully.");
                System.out.println("Inventory updated successfully.");

            }

        } catch (SQLException e) {

            try {

                if (connection != null) {
                    connection.rollback();
                }

            } catch (SQLException rollbackException) {

                rollbackException.printStackTrace();

            }

            System.out.println("Unable to place raw material order.");
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

    public static void showInventoryList() {

        String sql = """
            SELECT i.inventory_id,
                   i.inventory_name,
                   rm.material_name
            FROM inventory i
            INNER JOIN raw_materials rm
                ON i.material_id = rm.material_id
            ORDER BY i.inventory_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tCURRENT INVENTORY");
            System.out.println("==========================================");
            System.out.println();

            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Inventory Name", 28)
                            + "Raw Material"
            );
            System.out.println("------------------------------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("inventory_id")), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("inventory_name"), 28)
                                + resultSet.getString("material_name")
                );

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load inventory.");
            e.printStackTrace();

        }

    }

}