package reports;

import database.DatabaseConnection;

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

            exportDeliveries(writer);

            ReportExporter.closeReport(writer);

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

    private void exportDeliveries(BufferedWriter writer)
            throws Exception {

        writer.write("=========== DELIVERIES ===========");
        writer.newLine();
        writer.newLine();

        String sql = """
                SELECT
                    po.order_number,
                    p.product_name,
                    d.delivered_quantity,
                    d.delivered_to,
                    d.delivery_date
                FROM deliveries d
                INNER JOIN production_orders po
                    ON d.order_id = po.order_id
                INNER JOIN products p
                    ON po.product_id = p.product_id
                ORDER BY d.delivery_date DESC
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
                        + resultSet.getInt("delivered_quantity"));
                writer.newLine();

                writer.write("Delivered To : "
                        + resultSet.getString("delivered_to"));
                writer.newLine();

                writer.write("Date : "
                        + resultSet.getString("delivery_date"));
                writer.newLine();

                writer.write("----------------------------------------------");
                writer.newLine();

            }

            writer.newLine();

        }

    }
}
