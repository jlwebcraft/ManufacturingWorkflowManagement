import java.sql.*;

public class UserManager {

    public static void addUser(User user) {

        String sql = """
                INSERT INTO users
                (role_id, name, username, email, password_hash, phone)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, user.getRoleId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, user.getEmail());

            String hashedPassword =
                    PasswordHasher.hashPassword(user.getPassword());

            preparedStatement.setString(5, hashedPassword);

            preparedStatement.setString(6, user.getPhone());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ User added successfully.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to add user.");
            e.printStackTrace();

        }

    }

    public static boolean usernameExists(String username) {

        String sql = """
            SELECT user_id
            FROM users
            WHERE username = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, username);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static boolean emailExists(String email) {

        String sql = """
            SELECT user_id
            FROM users
            WHERE email = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, email);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static boolean userExists(int userId) {

        String sql = """
            SELECT user_id
            FROM users
            WHERE user_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, userId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }
        return false;
    }

    public static void viewUsers() {

        String sql = """
            SELECT u.user_id,
                   r.role_name,
                   u.name,
                   u.username,
                   u.email,
                   u.phone,
                   u.status
            FROM users u
            INNER JOIN roles r
            ON u.role_id = r.role_id
            WHERE u.status = 'ACTIVE'
            ORDER BY u.user_id
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
            System.out.println("\tAVAILABLE USERS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tRole\tName\tUsername");
            System.out.println("------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("user_id") + "\t"
                                + resultSet.getString("role_name") + "\t"
                                + resultSet.getString("name") + "\t"
                                + resultSet.getString("username"));

            }

            if (!found) {

                System.out.println("No users found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to view users.");
            e.printStackTrace();

        }

    }

    public static void updateUser(User user) {

        String sql = """
            UPDATE users
            SET role_id = ?,
                name = ?,
                username = ?,
                email = ?,
                password_hash = ?,
                phone = ?
            WHERE user_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, user.getRoleId());
            preparedStatement.setString(2, user.getName());
            preparedStatement.setString(3, user.getUsername());
            preparedStatement.setString(4, user.getEmail());

            String hashedPassword =
                    PasswordHasher.hashPassword(user.getPassword());

            preparedStatement.setString(5, hashedPassword);

            preparedStatement.setString(6, user.getPhone());

            preparedStatement.setInt(7, user.getUserId());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ User updated successfully.");

            } else {

                System.out.println("Invalid User ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to update user.");
            e.printStackTrace();

        }

    }

    public static void deleteUser(int userId) {

        String sql = """
            UPDATE users
            SET status = 'INACTIVE'
            WHERE user_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, userId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ User deleted successfully.");

            } else {

                System.out.println("Invalid User ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to delete user.");
            e.printStackTrace();

        }

    }

    public static void searchUser(String keyword) {

        String sql = """
            SELECT u.user_id,
                   r.role_name,
                   u.name,
                   u.username,
                   u.status
            FROM users u
            INNER JOIN roles r
            ON u.role_id = r.role_id
            WHERE u.name LIKE ?
            AND u.status = 'ACTIVE'
            ORDER BY u.name
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

            System.out.println("ID\tRole\tName\tUsername");
            System.out.println("----------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("user_id") + "\t"
                                + resultSet.getString("role_name") + "\t"
                                + resultSet.getString("name") + "\t"
                                + resultSet.getString("username"));

            }

            if (!found) {

                System.out.println("No matching user found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to search user.");
            e.printStackTrace();

        }

    }

    public static void showUserList() {

        String sql = """
            SELECT user_id,
                   name
            FROM users
            WHERE status = 'ACTIVE'
            ORDER BY user_id
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
            System.out.println("\tAVAILABLE USERS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tName");
            System.out.println("------------------------");

            while (resultSet.next()) {

                System.out.println(
                        resultSet.getInt("user_id")
                                + "\t"
                                + resultSet.getString("name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load users.");
            e.printStackTrace();

        }

    }

}