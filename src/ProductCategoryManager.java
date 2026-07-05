import java.sql.*;

public class ProductCategoryManager {

    public static void addCategory(ProductCategory category) {

        String sql = """
                INSERT INTO product_categories
                (category_name, description)
                VALUES (?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, category.getCategoryName());
            preparedStatement.setString(2, category.getDescription());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println();
                System.out.println("Category Added Successfully!");

            }

        }

        catch (SQLException e) {

            System.out.println("Unable to Add Category!");
            e.printStackTrace();

        }

    }

    public static void viewCategories() {

        String sql = """
            SELECT category_id,
                   category_name,
                   description
            FROM product_categories
            ORDER BY category_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("============= AVAILABLE CATEGORIES =============");
            System.out.println();
            System.out.println("ID\tCategory Name\t\tDescription");
            System.out.println("--------------------------------------------------------------");

            boolean found=false;

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("category_id") + "\t"
                                + resultSet.getString("category_name") + "\t\t"
                                + resultSet.getString("description"));

            }

            if (!found) {

                System.out.println("No Categories Found.");

            }

        }

        catch (SQLException e) {

            System.out.println("Unable to View Categories!");
            e.printStackTrace();

        }

    }

    public static void updateCategory(int categoryId, String newName, String newDescription) {

        String sql = """
            UPDATE product_categories
            SET category_name = ?, description = ?
            WHERE category_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, newName);
            preparedStatement.setString(2, newDescription);
            preparedStatement.setInt(3, categoryId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {
                System.out.println("✓ Category updated successfully.");
            } else {
                System.out.println("✗ Category ID not found.");
            }

        } catch (SQLException e) {

            System.out.println("Unable to update category.");
            e.printStackTrace();

        }

    }

    public static void deleteCategory(int categoryId) {

        String checkSql = """
            SELECT COUNT(*)
            FROM products
            WHERE category_id = ?
            """;

        String deleteSql = """
            DELETE FROM product_categories
            WHERE category_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement checkStatement =
                        connection.prepareStatement(checkSql);
                PreparedStatement deleteStatement =
                        connection.prepareStatement(deleteSql)
        ) {

            checkStatement.setInt(1, categoryId);

            ResultSet resultSet = checkStatement.executeQuery();

            if (resultSet.next()) {

                int count = resultSet.getInt(1);

                if (count > 0) {

                    System.out.println("Cannot delete category.");
                    System.out.println("This category is assigned to one or more products.");

                    resultSet.close();
                    return;

                }

            }

            resultSet.close();

            deleteStatement.setInt(1, categoryId);

            int rows = deleteStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Category deleted successfully.");

            } else {

                System.out.println("✗ Category ID not found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to delete category.");
            e.printStackTrace();

        }

    }

    public static void searchCategory(String keyword) {

        String sql = """
            SELECT category_id,
                   category_name,
                   description
            FROM product_categories
            WHERE category_name LIKE ?
            ORDER BY category_name
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, "%" + keyword + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean found = false;

            System.out.println();
            System.out.println("============= SEARCH RESULTS =============");
            System.out.println();
            System.out.println("ID\tCategory Name\t\tDescription");
            System.out.println("--------------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        resultSet.getInt("category_id") + "\t"
                                + resultSet.getString("category_name") + "\t\t"
                                + resultSet.getString("description"));

            }

            if (!found) {
                System.out.println("No matching category found.");
            }

            resultSet.close();

        } catch (SQLException e) {

            System.out.println("Unable to search category.");
            e.printStackTrace();

        }

    }

    public static void showCategoryList() {

        String sql = """
            SELECT category_id,
                   category_name
            FROM product_categories
            ORDER BY category_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tAVAILABLE CATEGORIES");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tCategory Name");
            System.out.println("------------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        resultSet.getInt("category_id") + "\t"
                                + resultSet.getString("category_name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load categories.");
            e.printStackTrace();

        }

    }

    public static boolean categoryExists(int categoryId) {

        String sql = """
            SELECT category_id
            FROM product_categories
            WHERE category_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, categoryId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            System.out.println("Unable to verify category.");
            e.printStackTrace();

        }

        return false;

    }

}