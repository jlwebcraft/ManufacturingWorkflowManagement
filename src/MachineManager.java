import java.sql.*;

public class MachineManager {

    public static void addMachine(Machine machine) {

        String sql = """
                INSERT INTO machines
                (machine_name,
                 machine_type,
                 daily_capacity,
                 capacity_unit,
                 purchase_date,
                 last_service_date,
                 status)
                VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, machine.getMachineName());
            preparedStatement.setString(2, machine.getMachineType());
            preparedStatement.setInt(3, machine.getDailyCapacity());
            preparedStatement.setString(4, machine.getCapacityUnit());
            preparedStatement.setDate(5, Date.valueOf(machine.getPurchaseDate()));
            preparedStatement.setDate(6, Date.valueOf(machine.getLastServiceDate()));
            preparedStatement.setString(7, "AVAILABLE");

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Machine added successfully.");

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
               capacity_unit,
               purchase_date,
               last_service_date,
               status
        FROM machines
        WHERE status = 'AVAILABLE'
        ORDER BY machine_id
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
            System.out.println("\t\tAVAILABLE MACHINES");
            System.out.println("==================================================");

            while (resultSet.next()) {

                found = true;

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("Machine ID        : "
                        + resultSet.getInt("machine_id"));
                System.out.println("Machine Name      : "
                        + resultSet.getString("machine_name"));
                System.out.println("Machine Type      : "
                        + resultSet.getString("machine_type"));
                System.out.println("Daily Capacity    : "
                        + resultSet.getInt("daily_capacity")
                        + " "
                        + resultSet.getString("capacity_unit"));
                System.out.println("Purchase Date     : "
                        + resultSet.getDate("purchase_date"));
                System.out.println("Last Service Date : "
                        + resultSet.getDate("last_service_date"));
                System.out.println("Status            : "
                        + resultSet.getString("status"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println();
                System.out.println("No machines found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to view machines.");
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
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, machineId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void updateMachine(Machine machine) {

        String sql = """
            UPDATE machines
            SET machine_name = ?,
                machine_type = ?,
                daily_capacity = ?,
                capacity_unit = ?,
                purchase_date = ?,
                last_service_date = ?
            WHERE machine_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, machine.getMachineName());
            preparedStatement.setString(2, machine.getMachineType());
            preparedStatement.setInt(3, machine.getDailyCapacity());
            preparedStatement.setString(4, machine.getCapacityUnit());
            preparedStatement.setDate(5, Date.valueOf(machine.getPurchaseDate()));
            preparedStatement.setDate(6, Date.valueOf(machine.getLastServiceDate()));
            preparedStatement.setInt(7, machine.getMachineId());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Machine updated successfully.");

            } else {

                System.out.println("Invalid Machine ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to update machine.");
            e.printStackTrace();

        }

    }

    public static void deleteMachine(int machineId) {

        String sql = """
            UPDATE machines
            SET status = 'INACTIVE'
            WHERE machine_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, machineId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Machine deleted successfully.");

            } else {

                System.out.println("Invalid Machine ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to delete machine.");
            e.printStackTrace();

        }

    }

    public static void searchMachine(String keyword) {

        String sql = """
        SELECT machine_id,
               machine_name,
               machine_type,
               daily_capacity,
               capacity_unit,
               purchase_date,
               last_service_date,
               status
        FROM machines
        WHERE machine_name LIKE ?
        AND status = 'AVAILABLE'
        ORDER BY machine_name
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
                System.out.println("Machine ID        : "
                        + resultSet.getInt("machine_id"));
                System.out.println("Machine Name      : "
                        + resultSet.getString("machine_name"));
                System.out.println("Machine Type      : "
                        + resultSet.getString("machine_type"));
                System.out.println("Daily Capacity    : "
                        + resultSet.getInt("daily_capacity")
                        + " "
                        + resultSet.getString("capacity_unit"));
                System.out.println("Purchase Date     : "
                        + resultSet.getDate("purchase_date"));
                System.out.println("Last Service Date : "
                        + resultSet.getDate("last_service_date"));
                System.out.println("Status            : "
                        + resultSet.getString("status"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println("No matching machine found.");

            }

            resultSet.close();

        } catch (SQLException e) {

            System.out.println("Unable to search machine.");
            e.printStackTrace();

        }

    }

    public static void showMachineList() {

        String sql = """
        SELECT machine_id,
               machine_name
        FROM machines
        WHERE status = 'AVAILABLE'
        ORDER BY machine_id
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
            System.out.println("\tAVAILABLE MACHINES");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tMachine Name");
            System.out.println("------------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        ConsoleFormatter.padRight(
                                String.valueOf(resultSet.getInt("machine_id")), 5)
                                + resultSet.getString("machine_name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load machines.");
            e.printStackTrace();

        }

    }

}