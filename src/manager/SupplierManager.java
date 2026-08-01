import java.sql.*;

public class SupplierManager {

    public static void addSupplier(Supplier supplier) {

        String sql = """
                INSERT INTO suppliers
                (supplier_name, contact_person, phone, email, address)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getContactPerson());
            preparedStatement.setString(3, supplier.getPhone());
            preparedStatement.setString(4, supplier.getEmail());
            preparedStatement.setString(5, supplier.getAddress());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Supplier added successfully.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to add supplier.");
            e.printStackTrace();

        }

    }

    public static void viewSuppliers() {

        String sql = """
        SELECT supplier_id,
               supplier_name,
               contact_person,
               phone,
               email,
               address,
               status
        FROM suppliers
        WHERE status = 'ACTIVE'
        ORDER BY supplier_id
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
            System.out.println("\t\tAVAILABLE SUPPLIERS");
            System.out.println("==================================================");

            while (resultSet.next()) {

                found = true;

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("Supplier ID    : "
                        + resultSet.getInt("supplier_id"));
                System.out.println("Supplier Name  : "
                        + resultSet.getString("supplier_name"));
                System.out.println("Contact Person : "
                        + resultSet.getString("contact_person"));
                System.out.println("Phone          : "
                        + resultSet.getString("phone"));
                System.out.println("Email          : "
                        + resultSet.getString("email"));
                System.out.println("Address        : "
                        + resultSet.getString("address"));
                System.out.println("Status         : "
                        + resultSet.getString("status"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println();
                System.out.println("No suppliers found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to view suppliers.");
            e.printStackTrace();

        }

    }

    public static boolean supplierExists(int supplierId) {

        String sql = """
            SELECT supplier_id
            FROM suppliers
            WHERE supplier_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, supplierId);

            ResultSet resultSet = preparedStatement.executeQuery();

            boolean exists = resultSet.next();

            resultSet.close();

            return exists;

        } catch (SQLException e) {

            e.printStackTrace();

        }

        return false;

    }

    public static void updateSupplier(Supplier supplier) {

        String sql = """
            UPDATE suppliers
            SET supplier_name = ?,
                contact_person = ?,
                phone = ?,
                email = ?,
                address = ?
            WHERE supplier_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setString(1, supplier.getSupplierName());
            preparedStatement.setString(2, supplier.getContactPerson());
            preparedStatement.setString(3, supplier.getPhone());
            preparedStatement.setString(4, supplier.getEmail());
            preparedStatement.setString(5, supplier.getAddress());
            preparedStatement.setInt(6, supplier.getSupplierId());

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Supplier updated successfully.");

            } else {

                System.out.println("Invalid Supplier ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to update supplier.");
            e.printStackTrace();

        }

    }

    public static void deleteSupplier(int supplierId) {

        String sql = """
            UPDATE suppliers
            SET status = 'INACTIVE'
            WHERE supplier_id = ?
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)
        ) {

            preparedStatement.setInt(1, supplierId);

            int rows = preparedStatement.executeUpdate();

            if (rows > 0) {

                System.out.println("✓ Supplier deleted successfully.");

            } else {

                System.out.println("Invalid Supplier ID.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to delete supplier.");
            e.printStackTrace();

        }

    }

    public static void searchSupplier(String keyword) {

        String sql = """
        SELECT supplier_id,
               supplier_name,
               contact_person,
               phone,
               email,
               address,
               status
        FROM suppliers
        WHERE supplier_name LIKE ?
        AND status = 'ACTIVE'
        ORDER BY supplier_name
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
                System.out.println("Supplier ID    : "
                        + resultSet.getInt("supplier_id"));
                System.out.println("Supplier Name  : "
                        + resultSet.getString("supplier_name"));
                System.out.println("Contact Person : "
                        + resultSet.getString("contact_person"));
                System.out.println("Phone          : "
                        + resultSet.getString("phone"));
                System.out.println("Email          : "
                        + resultSet.getString("email"));
                System.out.println("Address        : "
                        + resultSet.getString("address"));
                System.out.println("Status         : "
                        + resultSet.getString("status"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println("No matching supplier found.");

            }

            resultSet.close();

        } catch (SQLException e) {

            System.out.println("Unable to search supplier.");
            e.printStackTrace();

        }

    }

    public static void showSupplierList() {

        String sql = """
            SELECT supplier_id,
                   supplier_name
            FROM suppliers
            WHERE status = 'ACTIVE'
            ORDER BY supplier_id
            """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                ResultSet resultSet = preparedStatement.executeQuery()
        ) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tAVAILABLE SUPPLIERS");
            System.out.println("==========================================");
            System.out.println();

            System.out.println("ID\tSupplier");
            System.out.println("----------------------------------------");

            while (resultSet.next()) {

                System.out.println(
                        ConsoleFormatter.padRight(
                                String.valueOf(resultSet.getInt("supplier_id")), 5)
                                + resultSet.getString("supplier_name"));

            }

            System.out.println();

        } catch (SQLException e) {

            System.out.println("Unable to load suppliers.");
            e.printStackTrace();

        }

    }

}