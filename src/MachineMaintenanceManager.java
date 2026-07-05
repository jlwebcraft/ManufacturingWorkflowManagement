import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MachineMaintenanceManager {

    public static void addMaintenanceRecord(MachineMaintenance maintenance) {

        String sql = """
                INSERT INTO machine_maintenance
                (machine_id,
                 maintenance_date,
                 maintenance_type,
                 technician,
                 cost,
                 remarks,
                 next_service_date)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, maintenance.getMachineId());
            preparedStatement.setString(2, maintenance.getMaintenanceDate());
            preparedStatement.setString(3, maintenance.getMaintenanceType());
            preparedStatement.setString(4, maintenance.getTechnician());
            preparedStatement.setDouble(5, maintenance.getCost());
            preparedStatement.setString(6, maintenance.getRemarks());
            preparedStatement.setString(7, maintenance.getNextServiceDate());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println();
                System.out.println("Maintenance record added successfully.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to add maintenance record.");
            e.printStackTrace();

        }

    }

    public static void viewMaintenanceHistory() {

        String sql = """
                SELECT
                    mm.maintenance_id,
                    m.machine_name,
                    mm.maintenance_date,
                    mm.maintenance_type,
                    mm.technician,
                    mm.cost,
                    mm.next_service_date
                FROM machine_maintenance mm
                INNER JOIN machines m
                    ON mm.machine_id = m.machine_id
                ORDER BY mm.maintenance_date DESC
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
            System.out.println("==========================================");
            System.out.println("\tMAINTENANCE HISTORY");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tMachine\t\tDate\t\tType");
            System.out.println("-------------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("maintenance_id") + "\t"
                                + resultSet.getString("machine_name") + "\t"
                                + resultSet.getString("maintenance_date") + "\t"
                                + resultSet.getString("maintenance_type")
                );

            }

            if (!found) {

                System.out.println("No maintenance records found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to load maintenance history.");
            e.printStackTrace();

        }

    }

    public static void searchMaintenanceHistory(int machineId) {

        String sql = """
                SELECT
                    mm.maintenance_id,
                    m.machine_name,
                    mm.maintenance_date,
                    mm.maintenance_type,
                    mm.technician,
                    mm.cost,
                    mm.remarks,
                    mm.next_service_date
                FROM machine_maintenance mm
                INNER JOIN machines m
                    ON mm.machine_id = m.machine_id
                WHERE mm.machine_id = ?
                ORDER BY mm.maintenance_date DESC
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, machineId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean found = false;

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tSEARCH RESULTS");
            System.out.println("==========================================");
            System.out.println();

            while (resultSet.next()) {

                found = true;

                System.out.println("------------------------------------------");
                System.out.println("Maintenance ID : "
                        + resultSet.getInt("maintenance_id"));

                System.out.println("Machine        : "
                        + resultSet.getString("machine_name"));

                System.out.println("Date           : "
                        + resultSet.getString("maintenance_date"));

                System.out.println("Type           : "
                        + resultSet.getString("maintenance_type"));

                System.out.println("Technician     : "
                        + resultSet.getString("technician"));

                System.out.println("Cost           : "
                        + resultSet.getDouble("cost"));

                System.out.println("Remarks        : "
                        + resultSet.getString("remarks"));

                System.out.println("Next Service   : "
                        + resultSet.getString("next_service_date"));

            }

            resultSet.close();

            if (!found) {

                System.out.println("No maintenance history found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to search maintenance history.");
            e.printStackTrace();

        }

    }

}