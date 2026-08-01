import java.sql.*;

public class InventoryManager {

    public static void addInventoryRecord(Inventory inventory) {

        String sql = """
                INSERT INTO inventory
                (material_id, current_stock, minimum_stock, maximum_stock)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, inventory.getMaterialId());
            preparedStatement.setDouble(2, inventory.getCurrentStock());
            preparedStatement.setDouble(3, inventory.getMinimumStock());
            preparedStatement.setDouble(4, inventory.getMaximumStock());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("✓ Inventory record created successfully.");
            }

        } catch (SQLException e) {
            System.out.println("Unable to create inventory record.");
            e.printStackTrace();
        }

    }

    public static void viewInventory() {

        String sql = """
        SELECT i.inventory_id,
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
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        preparedStatement.executeQuery()
        ) {

            boolean found = false;

            System.out.println();
            System.out.println("==================================================");
            System.out.println("\t\tCURRENT INVENTORY");
            System.out.println("==================================================");

            while (resultSet.next()) {

                found = true;

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("Inventory ID   : "
                        + resultSet.getInt("inventory_id"));
                System.out.println("Material       : "
                        + resultSet.getString("material_name"));
                System.out.println("Current Stock  : "
                        + resultSet.getDouble("current_stock"));
                System.out.println("Minimum Stock  : "
                        + resultSet.getDouble("minimum_stock"));
                System.out.println("Maximum Stock  : "
                        + resultSet.getDouble("maximum_stock"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println();
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
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, inventoryId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

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
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, inventoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("✓ Stock added successfully.");
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
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setDouble(1, quantity);
            preparedStatement.setInt(2, inventoryId);
            preparedStatement.setDouble(3, quantity);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Stock removed successfully.");

            } else {

                System.out.println("Insufficient stock.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to remove stock.");
            e.printStackTrace();

        }

    }

    public static void searchInventory(String keyword) {

        String sql = """
        SELECT i.inventory_id,
               rm.material_name,
               i.current_stock,
               i.minimum_stock,
               i.maximum_stock
        FROM inventory i
        INNER JOIN raw_materials rm
        ON i.material_id = rm.material_id
        WHERE rm.material_name LIKE ?
        ORDER BY rm.material_name
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
                System.out.println("Inventory ID   : "
                        + resultSet.getInt("inventory_id"));
                System.out.println("Material       : "
                        + resultSet.getString("material_name"));
                System.out.println("Current Stock  : "
                        + resultSet.getDouble("current_stock"));
                System.out.println("Minimum Stock  : "
                        + resultSet.getDouble("minimum_stock"));
                System.out.println("Maximum Stock  : "
                        + resultSet.getDouble("maximum_stock"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println("No matching inventory found.");

            }

            resultSet.close();

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
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, materialId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void showInventoryList() {

        String sql = """
        SELECT i.inventory_id,
               rm.material_name
        FROM inventory i
        INNER JOIN raw_materials rm
        ON i.material_id = rm.material_id
        ORDER BY i.inventory_id
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
            System.out.println("\tCURRENT INVENTORY");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tMaterial");
            System.out.println("------------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        ConsoleFormatter.padRight(
                                String.valueOf(resultSet.getInt("inventory_id")), 5)
                                + resultSet.getString("material_name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load inventory.");
            e.printStackTrace();

        }

    }

}