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
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            boolean found = false;

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tCURRENT INVENTORY");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tMaterial\tStock\tMin\tMax");
            System.out.println("------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("inventory_id") + "\t"
                                + resultSet.getString("material_name") + "\t"
                                + resultSet.getDouble("current_stock") + "\t"
                                + resultSet.getDouble("minimum_stock") + "\t"
                                + resultSet.getDouble("maximum_stock"));

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
            System.out.println("==========================================");
            System.out.println("\tSEARCH RESULTS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tMaterial\tStock\tMin\tMax");
            System.out.println("------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("inventory_id") + "\t"
                                + resultSet.getString("material_name") + "\t"
                                + resultSet.getDouble("current_stock") + "\t"
                                + resultSet.getDouble("minimum_stock") + "\t"
                                + resultSet.getDouble("maximum_stock"));

            }

            if (!found) {

                System.out.println("No matching inventory found.");

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

}