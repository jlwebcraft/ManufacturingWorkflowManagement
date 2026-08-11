package manager;

import database.DatabaseConnection;
import model.RawMaterial;
import util.ConsoleFormatter;

import java.sql.*;

public class RawMaterialManager {

    public static void addRawMaterial(RawMaterial rawMaterial) {

        String sql = """
                INSERT INTO raw_materials
                (material_name, unit, cost_per_unit)
                VALUES (?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, rawMaterial.getMaterialName());
            preparedStatement.setString(2, rawMaterial.getUnit());
            preparedStatement.setDouble(3, rawMaterial.getCostPerUnit());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("Raw Material added successfully.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to add raw material.");
            e.printStackTrace();

        }

    }

    public static void viewRawMaterials() {

        String sql = """
        SELECT material_id,
               material_name,
               unit,
               cost_per_unit,
               status
        FROM raw_materials
        WHERE status = 'ACTIVE'
        ORDER BY material_id
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
            System.out.println("\t\tAVAILABLE RAW MATERIALS");
            System.out.println("==================================================");

            while (resultSet.next()) {

                found = true;

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("Material ID    : "
                        + resultSet.getInt("material_id"));
                System.out.println("Material Name  : "
                        + resultSet.getString("material_name"));
                System.out.println("Unit           : "
                        + resultSet.getString("unit"));
                System.out.println("Cost Per Unit  : "
                        + String.format("%,.2f", resultSet.getDouble("cost_per_unit")));
                System.out.println("Status         : "
                        + resultSet.getString("status"));
                System.out.println("--------------------------------------------------");
            }
            if (!found) {
                System.out.println();
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
            AND status = 'ACTIVE'
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
            SET material_name = ?,
                unit = ?,
                cost_per_unit = ?
            WHERE material_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, rawMaterial.getMaterialName());
            preparedStatement.setString(2, rawMaterial.getUnit());
            preparedStatement.setDouble(3, rawMaterial.getCostPerUnit());
            preparedStatement.setInt(4, rawMaterial.getMaterialId());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("Raw Material updated successfully.");

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

                System.out.println("Raw Material deleted successfully.");

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
        SELECT material_id,
               material_name,
               unit,
               cost_per_unit,
               status
        FROM raw_materials
        WHERE material_name LIKE ?
        AND status = 'ACTIVE'
        ORDER BY material_name
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
                System.out.println("Material ID    : "
                        + resultSet.getInt("material_id"));
                System.out.println("Material Name  : "
                        + resultSet.getString("material_name"));
                System.out.println("Unit           : "
                        + resultSet.getString("unit"));
                System.out.println("Cost Per Unit  : "
                        + String.format("%,.2f", resultSet.getDouble("cost_per_unit")));
                System.out.println("Status         : "
                        + resultSet.getString("status"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println("No matching raw material found.");

            }

            resultSet.close();

        } catch (SQLException e) {

            System.out.println("Unable to search raw material.");
            e.printStackTrace();

        }

    }
    public static void showRawMaterialList() {

        String sql = """
            SELECT material_id,
                   material_name,
                   unit,
                   cost_per_unit
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

            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Raw Material", 28)
                            + ConsoleFormatter.padRight("Unit", 12)
                            + "Cost Per Unit"
            );
            System.out.println("------------------------------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        ConsoleFormatter.padRight(
                                String.valueOf(resultSet.getInt("material_id")), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("material_name"), 28)
                                + ConsoleFormatter.padRight(resultSet.getString("unit"), 12)
                                + String.format("%,.2f", resultSet.getDouble("cost_per_unit"))
                );

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load raw materials.");
            e.printStackTrace();

        }

    }

}