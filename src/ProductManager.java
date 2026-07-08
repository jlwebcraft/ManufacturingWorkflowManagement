import java.sql.*;
import java.util.*;

public class ProductManager {

    public static void addProduct(Product product) {

        String sql = """
                INSERT INTO products
                (category_id, product_name, description,
                 estimated_production_hours, selling_price)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, product.getCategoryId());
            preparedStatement.setString(2, product.getProductName());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setInt(4, product.getEstimatedProductionHours());
            preparedStatement.setDouble(5, product.getSellingPrice());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println();
                System.out.println("✓ Product added successfully.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to add product.");
            e.printStackTrace();

        }

    }

    public static void viewProducts() {

        final int PAGE_SIZE = 10;

        Scanner scanner = new Scanner(System.in);

        String countSql = """
            SELECT COUNT(*)
            FROM products
            WHERE status = 'ACTIVE'
            """;

        String dataSql = """
            SELECT p.product_id,
                   c.category_name,
                   p.product_name,
                   p.description,
                   p.estimated_production_hours,
                   p.selling_price,
                   p.status
            FROM products p
            INNER JOIN product_categories c
            ON p.category_id = c.category_id
            WHERE p.status = 'ACTIVE'
            ORDER BY p.product_id
            LIMIT ? OFFSET ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase()
        ) {

            int totalProducts = 0;

            try (
                    PreparedStatement countStatement =
                            connection.prepareStatement(countSql);
                    ResultSet countResult =
                            countStatement.executeQuery()
            ) {

                if (countResult.next()) {

                    totalProducts = countResult.getInt(1);

                }

            }

            if (totalProducts == 0) {

                System.out.println();
                System.out.println("No products found.");
                return;

            }

            int totalPages = (int) Math.ceil((double) totalProducts / PAGE_SIZE);

            int currentPage = 1;

            while (true) {

                int offset = (currentPage - 1) * PAGE_SIZE;

                try (
                        PreparedStatement preparedStatement =
                                connection.prepareStatement(dataSql)
                ) {

                    preparedStatement.setInt(1, PAGE_SIZE);
                    preparedStatement.setInt(2, offset);

                    try (
                            ResultSet resultSet =
                                    preparedStatement.executeQuery()
                    ) {

                        System.out.println();
                        System.out.println("==================================================");
                        System.out.println("\t\tAVAILABLE PRODUCTS");
                        System.out.println("\t\tPage "
                                + currentPage
                                + " of "
                                + totalPages);
                        System.out.println("==================================================");

                        while (resultSet.next()) {

                            System.out.println();
                            System.out.println("--------------------------------------------------");
                            System.out.println("Product ID      : "
                                    + resultSet.getInt("product_id"));

                            System.out.println("Category        : "
                                    + resultSet.getString("category_name"));

                            System.out.println("Product Name    : "
                                    + resultSet.getString("product_name"));

                            System.out.println("Description     : "
                                    + resultSet.getString("description"));

                            System.out.println("Production Time : "
                                    + resultSet.getInt("estimated_production_hours")
                                    + " Hours");

                            System.out.println("Selling Price   : "
                                    + String.format("₹%,.2f",
                                    resultSet.getDouble("selling_price")));

                            System.out.println("Status          : "
                                    + resultSet.getString("status"));

                            System.out.println("--------------------------------------------------");

                        }

                    }

                }

                System.out.println();
                System.out.println("==================================================");

                if (currentPage > 1) {

                    System.out.print("[P] Previous  ");

                }

                if (currentPage < totalPages) {

                    System.out.print("[N] Next  ");

                }

                System.out.print("[Q] Back");

                System.out.println();
                System.out.print("Choice: ");

                String choice = scanner.nextLine().trim().toUpperCase();

                switch (choice) {

                    case "N":

                        if (currentPage < totalPages) {

                            currentPage++;

                        }

                        break;

                    case "P":

                        if (currentPage > 1) {

                            currentPage--;

                        }

                        break;

                    case "Q":

                        return;

                    default:

                        System.out.println("Invalid Choice.");

                }

            }

        } catch (SQLException e) {

            System.out.println("Unable to view products.");
            e.printStackTrace();

        }

    }

    public static void updateProduct(Product product) {

        String sql = """
            UPDATE products
            SET category_id = ?,
                product_name = ?,
                description = ?,
                estimated_production_hours = ?,
                selling_price = ?
            WHERE product_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, product.getCategoryId());
            preparedStatement.setString(2, product.getProductName());
            preparedStatement.setString(3, product.getDescription());
            preparedStatement.setInt(4, product.getEstimatedProductionHours());
            preparedStatement.setDouble(5, product.getSellingPrice());
            preparedStatement.setInt(6, product.getProductId());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Product updated successfully.");

            } else {

                System.out.println("Product not found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to update product.");
            e.printStackTrace();

        }

    }

    public static boolean productExists(int productId) {

        String sql = """
            SELECT product_id
            FROM products
            WHERE product_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, productId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void deleteProduct(int productId) {

        String sql = """
            UPDATE products
            SET status = 'INACTIVE'
            WHERE product_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, productId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Product deleted successfully.");

            } else {

                System.out.println("Invalid Product ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to delete product.");
            e.printStackTrace();

        }

    }

    public static void searchProduct(String keyword) {

        String sql = """
            SELECT p.product_id,
                   c.category_name,
                   p.product_name,
                   p.selling_price,
                   p.status
            FROM products p
            INNER JOIN product_categories c
            ON p.category_id = c.category_id
            WHERE p.product_name LIKE ?
            ORDER BY p.product_name
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

            System.out.println(
                    ConsoleFormatter.padRight("ID", 5)
                            + ConsoleFormatter.padRight("Category", 28)
                            + ConsoleFormatter.padRight("Product", 40)
                            + ConsoleFormatter.padRight("Price", 12)
                            + "Status"
            );
            System.out.println("-----------------------------------------------------------------------------------------------");

            while (resultSet.next()) {

                found = true;

                System.out.println(
                        ConsoleFormatter.padRight(String.valueOf(resultSet.getInt("product_id")), 5)
                                + ConsoleFormatter.padRight(resultSet.getString("category_name"), 28)
                                + ConsoleFormatter.padRight(resultSet.getString("product_name"), 40)
                                + ConsoleFormatter.padRight(String.valueOf(resultSet.getDouble("selling_price")), 15)
                                + resultSet.getString("status")
                );

            }

            if (!found) {

                System.out.println("No matching product found.");

            }

            resultSet.close();

        } catch (SQLException e) {

            System.out.println("Unable to search product.");
            e.printStackTrace();

        }

    }

    public static void showProductList() {

        String sql = """
            SELECT product_id,
                   product_name
            FROM products
            WHERE status = 'ACTIVE'
            ORDER BY product_id
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
            System.out.println("\tAVAILABLE PRODUCTS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tProduct");
            System.out.println("------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        resultSet.getInt("product_id")
                                + "\t"
                                + resultSet.getString("product_name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load products.");
            e.printStackTrace();

        }

    }

}