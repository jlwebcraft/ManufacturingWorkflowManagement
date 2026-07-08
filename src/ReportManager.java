import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ReportManager {

    public static void inventoryReport() {

        String sql = """
            SELECT
                rm.material_name,
                i.current_stock,
                i.minimum_stock,
                i.maximum_stock,
                rm.unit
            FROM inventory i
            INNER JOIN raw_materials rm
                ON i.material_id = rm.material_id
            ORDER BY rm.material_name
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
            System.out.println("\t\tINVENTORY REPORT");
            System.out.println("==================================================");

            while (resultSet.next()) {

                found = true;

                System.out.println();
                System.out.println("--------------------------------------------------");
                System.out.println("Material        : "
                        + resultSet.getString("material_name"));
                System.out.println("Current Stock   : "
                        + resultSet.getDouble("current_stock") + " "
                        + resultSet.getString("unit"));
                System.out.println("Minimum Stock   : "
                        + resultSet.getDouble("minimum_stock") + " "
                        + resultSet.getString("unit"));
                System.out.println("Maximum Stock   : "
                        + resultSet.getDouble("maximum_stock") + " "
                        + resultSet.getString("unit"));
                System.out.println("--------------------------------------------------");

            }

            if (!found) {

                System.out.println();
                System.out.println("No inventory records found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to generate inventory report.");
            e.printStackTrace();

        }

    }

    public static void lowStockReport() {

        String sql = """
                SELECT
                    rm.material_name,
                    i.current_stock,
                    i.minimum_stock,
                    rm.unit
                FROM inventory i
                INNER JOIN raw_materials rm
                    ON i.material_id = rm.material_id
                WHERE i.current_stock <= i.minimum_stock
                ORDER BY rm.material_name
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
            System.out.println("\tLOW STOCK REPORT");
            System.out.println("==========================================");
            System.out.println();

            while (resultSet.next()) {

                found = true;

                System.out.println("------------------------------------------");
                System.out.println("Material : "
                        + resultSet.getString("material_name"));

                System.out.println("Current  : "
                        + resultSet.getDouble("current_stock") + " "
                        + resultSet.getString("unit"));

                System.out.println("Minimum  : "
                        + resultSet.getDouble("minimum_stock") + " "
                        + resultSet.getString("unit"));

                System.out.println("------------------------------------------");

            }

            if (!found) {

                System.out.println("No low stock materials.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to generate low stock report.");
            e.printStackTrace();

        }

    }

    public static void productionOrderReport() {

        String sql = """
                SELECT
                    po.order_number,
                    p.product_name,
                    po.quantity,
                    m.machine_name,
                    u.name,
                    po.status,
                    po.created_at
                FROM production_orders po
                INNER JOIN products p
                    ON po.product_id = p.product_id
                INNER JOIN machines m
                    ON po.machine_id = m.machine_id
                INNER JOIN users u
                    ON po.created_by = u.user_id
                ORDER BY po.created_at DESC
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
            System.out.println("\tPRODUCTION ORDER REPORT");
            System.out.println("==========================================");
            System.out.println();

            while (resultSet.next()) {

                found = true;

                System.out.println("------------------------------------------");
                System.out.println("Order No. : "
                        + resultSet.getString("order_number"));

                System.out.println("Product   : "
                        + resultSet.getString("product_name"));

                System.out.println("Quantity  : "
                        + resultSet.getInt("quantity"));

                System.out.println("Machine   : "
                        + resultSet.getString("machine_name"));

                System.out.println("Created By: "
                        + resultSet.getString("name"));

                System.out.println("Status    : "
                        + resultSet.getString("status"));

                System.out.println("Date      : "
                        + resultSet.getString("created_at"));

                System.out.println("------------------------------------------");

            }

            if (!found) {

                System.out.println("No production orders found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to generate production order report.");
            e.printStackTrace();

        }

    }

    public static void machineMaintenanceReport() {

        String sql = """
                SELECT
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
            System.out.println("\tMACHINE MAINTENANCE REPORT");
            System.out.println("==========================================");
            System.out.println();

            while (resultSet.next()) {

                found = true;

                System.out.println("------------------------------------------");
                System.out.println("Machine      : "
                        + resultSet.getString("machine_name"));

                System.out.println("Date         : "
                        + resultSet.getString("maintenance_date"));

                System.out.println("Type         : "
                        + resultSet.getString("maintenance_type"));

                System.out.println("Technician   : "
                        + resultSet.getString("technician"));

                System.out.println("Cost         : "
                        + resultSet.getDouble("cost"));

                System.out.println("Next Service : "
                        + resultSet.getString("next_service_date"));

                System.out.println("------------------------------------------");

            }

            if (!found) {

                System.out.println("No maintenance records found.");

            }

        } catch (SQLException e) {

            System.out.println("Unable to generate maintenance report.");
            e.printStackTrace();

        }

    }

}