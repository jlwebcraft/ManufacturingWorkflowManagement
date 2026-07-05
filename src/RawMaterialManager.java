import java.sql.*;

public class RawMaterialManager {

    public static void addRawMaterial(RawMaterial rawMaterial) {

        String sql = """
                INSERT INTO raw_materials
                (supplier_id, material_name, unit, cost_per_unit)
                VALUES (?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, rawMaterial.getSupplierId());
            preparedStatement.setString(2, rawMaterial.getMaterialName());
            preparedStatement.setString(3, rawMaterial.getUnit());
            preparedStatement.setDouble(4, rawMaterial.getCostPerUnit());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Raw Material added successfully.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to add raw material.");
            e.printStackTrace();

        }

    }

    public static void viewRawMaterials() {

        String sql = """
            SELECT rm.material_id,
                   s.supplier_name,
                   rm.material_name,
                   rm.unit,
                   rm.cost_per_unit,
                   rm.status
            FROM raw_materials rm
            INNER JOIN suppliers s
            ON rm.supplier_id = s.supplier_id
            WHERE rm.status = 'ACTIVE'
            ORDER BY rm.material_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            boolean found = false;

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tAVAILABLE RAW MATERIALS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tSupplier\tMaterial\tUnit\tCost");
            System.out.println("--------------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("material_id") + "\t"
                                + resultSet.getString("supplier_name") + "\t"
                                + resultSet.getString("material_name") + "\t"
                                + resultSet.getString("unit") + "\t"
                                + resultSet.getDouble("cost_per_unit"));

            }

            if (!found) {

                System.out.println("No raw materials found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to view raw materials.");
            e.printStackTrace();

        }

    }

    public static boolean rawMaterialExists(int materialId) {

        String sql = """
            SELECT material_id
            FROM raw_materials
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

    public static void updateRawMaterial(RawMaterial rawMaterial) {

        String sql = """
            UPDATE raw_materials
            SET supplier_id = ?,
                material_name = ?,
                unit = ?,
                cost_per_unit = ?
            WHERE material_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, rawMaterial.getSupplierId());
            preparedStatement.setString(2, rawMaterial.getMaterialName());
            preparedStatement.setString(3, rawMaterial.getUnit());
            preparedStatement.setDouble(4, rawMaterial.getCostPerUnit());
            preparedStatement.setInt(5, rawMaterial.getMaterialId());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Raw Material updated successfully.");

            } else {

                System.out.println("Invalid Material ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to update raw material.");
            e.printStackTrace();

        }

    }

    public static void deleteRawMaterial(int materialId) {

        String sql = """
            UPDATE raw_materials
            SET status = 'INACTIVE'
            WHERE material_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, materialId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Raw Material deleted successfully.");

            } else {

                System.out.println("Invalid Material ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to delete raw material.");
            e.printStackTrace();

        }

    }

    public static void searchRawMaterial(String keyword) {

        String sql = """
            SELECT rm.material_id,
                   s.supplier_name,
                   rm.material_name,
                   rm.unit,
                   rm.cost_per_unit,
                   rm.status
            FROM raw_materials rm
            INNER JOIN suppliers s
            ON rm.supplier_id = s.supplier_id
            WHERE rm.material_name LIKE ?
            AND rm.status = 'ACTIVE'
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

            System.out.println("ID\tSupplier\tMaterial\tUnit\tCost");
            System.out.println("--------------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("material_id") + "\t"
                                + resultSet.getString("supplier_name") + "\t"
                                + resultSet.getString("material_name") + "\t"
                                + resultSet.getString("unit") + "\t"
                                + resultSet.getDouble("cost_per_unit"));

            }

            if (!found) {

                System.out.println("No matching raw material found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to search raw material.");
            e.printStackTrace();

        }

    }

    public static void showRawMaterialList() {

        String sql = """
            SELECT material_id,
                   material_name
            FROM raw_materials
            WHERE status = 'ACTIVE'
            ORDER BY material_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tAVAILABLE RAW MATERIALS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tMaterial Name");
            System.out.println("------------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        resultSet.getInt("material_id")
                                + "\t"
                                + resultSet.getString("material_name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load raw materials.");
            e.printStackTrace();

        }

    }

}