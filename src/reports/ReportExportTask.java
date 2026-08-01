import java.io.*;
import java.sql.*;

public class ReportExportTask implements Runnable {

    private final int month;
    private final int year;

    public ReportExportTask(int month, int year) {

        this.month = month;
        this.year = year;

    }

    @Override
    public void run() {

        BufferedWriter writer = null;

        try {

            writer = ReportExporter.createMonthlyReport(month, year);

            exportInventory(writer);

            exportLowStock(writer);

            exportProductionOrders(writer);

            exportMachineMaintenance(writer);

            ReportExporter.closeReport(writer);

//            System.out.println();
//            System.out.println("==========================================");
//            System.out.println("Monthly report exported successfully.");
//            System.out.println("Location : reports/");
//            System.out.println("==========================================");

        } catch (Exception e) {

            System.out.println("Unable to export report.");
            e.printStackTrace();

        }

    }

    private void exportInventory(BufferedWriter writer)
            throws Exception {

        writer.write("============== INVENTORY REPORT ==============");
        writer.newLine();
        writer.newLine();

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

            while (resultSet.next()) {

                writer.write("Material : "
                        + resultSet.getString("material_name"));
                writer.newLine();

                writer.write("Current Stock : "
                        + resultSet.getDouble("current_stock")
                        + " "
                        + resultSet.getString("unit"));
                writer.newLine();

                writer.write("Minimum Stock : "
                        + resultSet.getDouble("minimum_stock"));

                writer.newLine();

                writer.write("Maximum Stock : "
                        + resultSet.getDouble("maximum_stock"));

                writer.newLine();

                writer.write("----------------------------------------------");
                writer.newLine();

            }

            writer.newLine();

        }

    }

    private void exportLowStock(BufferedWriter writer)
            throws Exception {

        writer.write("============== LOW STOCK REPORT ==============");
        writer.newLine();
        writer.newLine();

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

            while (resultSet.next()) {

                writer.write("Material : "
                        + resultSet.getString("material_name"));
                writer.newLine();

                writer.write("Current : "
                        + resultSet.getDouble("current_stock")
                        + " "
                        + resultSet.getString("unit"));
                writer.newLine();

                writer.write("Minimum : "
                        + resultSet.getDouble("minimum_stock")
                        + " "
                        + resultSet.getString("unit"));
                writer.newLine();

                writer.write("----------------------------------------------");
                writer.newLine();

            }

            writer.newLine();

        }

    }

    private void exportProductionOrders(BufferedWriter writer)
            throws Exception {

        writer.write("=========== PRODUCTION ORDERS ===========");
        writer.newLine();
        writer.newLine();

        String sql = """
                SELECT
                    po.order_number,
                    p.product_name,
                    po.quantity,
                    po.status
                FROM production_orders po
                INNER JOIN products p
                    ON po.product_id = p.product_id
                ORDER BY po.created_at DESC
                """;

        try (
                Connection connection = DatabaseConnection.connectDatabase();
                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql);
                ResultSet resultSet =
                        preparedStatement.executeQuery()
        ) {

            while (resultSet.next()) {

                writer.write("Order : "
                        + resultSet.getString("order_number"));
                writer.newLine();

                writer.write("Product : "
                        + resultSet.getString("product_name"));
                writer.newLine();

                writer.write("Quantity : "
                        + resultSet.getInt("quantity"));
                writer.newLine();

                writer.write("Status : "
                        + resultSet.getString("status"));
                writer.newLine();

                writer.write("----------------------------------------------");
                writer.newLine();

            }

            writer.newLine();

        }

    }

    private void exportMachineMaintenance(BufferedWriter writer)
            throws Exception {

        writer.write("========= MACHINE MAINTENANCE =========");
        writer.newLine();
        writer.newLine();

        String sql = """
                SELECT
                    m.machine_name,
                    mm.maintenance_date,
                    mm.maintenance_type,
                    mm.technician,
                    mm.cost
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

            while (resultSet.next()) {

                writer.write("Machine : "
                        + resultSet.getString("machine_name"));
                writer.newLine();

                writer.write("Date : "
                        + resultSet.getString("maintenance_date"));
                writer.newLine();

                writer.write("Type : "
                        + resultSet.getString("maintenance_type"));
                writer.newLine();

                writer.write("Technician : "
                        + resultSet.getString("technician"));
                writer.newLine();

                writer.write("Cost : ₹"
                        + resultSet.getDouble("cost"));
                writer.newLine();

                writer.write("----------------------------------------------");
                writer.newLine();

            }

            writer.newLine();

        }

    }

}