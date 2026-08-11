package manager;

import database.DatabaseConnection;
import util.ConsoleFormatter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MachineManager {

    public static void addMachine(String machineName,
                                  String machineType,
                                  int dailyCapacity) {

        String sql = """
            INSERT INTO machines
            (machine_name, machine_type, daily_capacity, status)
            VALUES (?, ?, ?, 'AVAILABLE')
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, machineName);
            preparedStatement.setString(2, machineType);
            preparedStatement.setInt(3, dailyCapacity);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Machine added successfully.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to add machine.");
            e.printStackTrace();
        }
    }

    public static void viewMachines() {

        String sql = """
            SELECT machine_id,
                   machine_name,
                   machine_type,
                   daily_capacity,
                   status
            FROM machines
            ORDER BY machine_id
            """;

        displayMachines(sql, "ALL MACHINES / RESOURCES");
    }

    public static void showAvailableMachines() {

        String sql = """
            SELECT machine_id,
                   machine_name,
                   machine_type,
                   daily_capacity,
                   status
            FROM machines
            WHERE status = 'AVAILABLE'
            ORDER BY machine_id
            """;

        displayMachines(sql, "AVAILABLE MACHINES");
    }

    public static void showMaintenanceMachines() {

        String sql = """
            SELECT machine_id,
                   machine_name,
                   machine_type,
                   daily_capacity,
                   status
            FROM machines
            WHERE status = 'MAINTENANCE'
            ORDER BY machine_id
            """;

        displayMachines(sql, "MACHINES UNDER MAINTENANCE");
    }

    public static void searchMachine(String keyword) {

        String sql = """
            SELECT machine_id,
                   machine_name,
                   machine_type,
                   daily_capacity,
                   status
            FROM machines
            WHERE machine_name LIKE ?
               OR machine_type LIKE ?
               OR status LIKE ?
            ORDER BY machine_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            String search = "%" + keyword + "%";
            preparedStatement.setString(1, search);
            preparedStatement.setString(2, search);
            preparedStatement.setString(3, search);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                printMachineResultSet(resultSet, "MACHINE SEARCH RESULTS");
            }
        } catch (SQLException e) {
            System.out.println("Unable to search machines.");
            e.printStackTrace();
        }
    }

    public static boolean machineExists(int machineId) {

        String sql = """
            SELECT machine_id
            FROM machines
            WHERE machine_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, machineId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Unable to validate machine.");
            e.printStackTrace();
        }

        return false;
    }

    public static boolean machineAvailable(int machineId) {

        String sql = """
            SELECT machine_id
            FROM machines
            WHERE machine_id = ?
              AND status = 'AVAILABLE'
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setInt(1, machineId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSet.next();
            }
        } catch (SQLException e) {
            System.out.println("Unable to validate machine availability.");
            e.printStackTrace();
        }

        return false;
    }

    public static void updateMachineStatus(int machineId, String status) {

        String sql = """
            UPDATE machines
            SET status = ?
            WHERE machine_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {
            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, machineId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("Machine status updated to " + status + ".");
            } else {
                System.out.println("Invalid Machine ID.");
            }
        } catch (SQLException e) {
            System.out.println("Unable to update machine status.");
            e.printStackTrace();
        }
    }

    public static void showMachineList() {
        viewMachines();
    }

    private static void displayMachines(String sql, String title) {
        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {
            printMachineResultSet(resultSet, title);
        } catch (SQLException e) {
            System.out.println("Unable to load machines.");
            e.printStackTrace();
        }
    }

    private static void printMachineResultSet(ResultSet resultSet, String title) throws SQLException {
        boolean found = false;

        System.out.println();
        System.out.println("==========================================");
        System.out.println("\t" + title);
        System.out.println("==========================================");
        System.out.println();
        System.out.println(
                ConsoleFormatter.padRight("ID", 5)
                        + ConsoleFormatter.padRight("Machine", 28)
                        + ConsoleFormatter.padRight("Type", 22)
                        + ConsoleFormatter.padRight("Capacity", 12)
                        + "Status"
        );
        System.out.println("--------------------------------------------------------------------------------");

        while (resultSet.next()) {
            found = true;
            System.out.println(
                    ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("machine_id")), 5)
                            + ConsoleFormatter.padRight(resultSet.getString("machine_name"), 28)
                            + ConsoleFormatter.padRight(resultSet.getString("machine_type"), 22)
                            + ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("daily_capacity")), 12)
                            + resultSet.getString("status")
            );
        }

        if (!found) {
            System.out.println("No machines found.");
        }
    }
}