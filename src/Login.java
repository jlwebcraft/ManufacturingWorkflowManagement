import java.sql.*;
import java.util.Scanner;

public class Login {

    public static boolean login() {

        Scanner scanner = new Scanner(System.in);

        System.out.println();
        System.out.println("==============================================");
        System.out.println(" Manufacturing Workflow Management System");
        System.out.println("==============================================");

        System.out.print("Username (type 'exit' to quit): ");
        String username = scanner.nextLine().trim();

        if (username.equalsIgnoreCase("exit")) {
            System.out.println("==========================================");
            System.out.println("Thank you for using MWMS.");
            System.out.println("Exiting...");
            System.out.println("==========================================");
            System.exit(0);
        }

        System.out.print("Password: ");
        String password = scanner.nextLine().trim();

        String hashedPassword = PasswordHasher.hashPassword(password);

        String sql = """
                SELECT name
                FROM users
                WHERE username = ?
                AND password_hash = ?
                AND status = 'ACTIVE'
                """;
        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql))
        {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, hashedPassword);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    System.out.println();
                    System.out.println("==============================================");
                    System.out.println("Login Successful!");
                    System.out.println("Welcome, " + resultSet.getString("name"));
                    System.out.println("==============================================");
                    System.out.println();
                    resultSet.close();
                    return true;
                }
            }
        }
        catch (SQLException e) {
            System.out.println("Database Error!");
            e.printStackTrace();
        }

        System.out.println();
        System.out.println("Invalid Username or Password!");
        System.out.println();
        return false;
    }
}