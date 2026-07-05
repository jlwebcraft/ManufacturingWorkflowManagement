import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RoleManager {

    public static void showRoles() {

        String sql = """
                SELECT role_id,
                       role_name
                FROM roles
                ORDER BY role_id
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tAVAILABLE ROLES");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tRole");
            System.out.println("--------------------------");

            while (resultSet.next()) {

                System.out.println(
                        resultSet.getInt("role_id")
                                + "\t"
                                + resultSet.getString("role_name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load roles.");
            e.printStackTrace();

        }

    }

    public static boolean roleExists(int roleId) {

        String sql = """
            SELECT role_id
            FROM roles
            WHERE role_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, roleId);

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