package thread;

import manager.ProductionQueueManager;
import model.ProductionOrder;
import database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ProductionWorker implements Runnable {

    private boolean running = true;

    @Override
    public void run() {

        while (running) {

            try {

                if (!ProductionQueueManager.isQueueEmpty()) {

                    ProductionOrder order =
                            ProductionQueueManager.getNextOrder();

                    updateOrderStatus(
                            order.getOrderId(),
                            "IN_PROGRESS"
                    );

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("MACHINE STARTED PRODUCTION");
                    System.out.println("==========================================");

                    System.out.println("Order Number : "
                            + order.getOrderNumber());

                    System.out.println("Priority     : "
                            + order.getPriority());

                    System.out.println("Product ID   : "
                            + order.getProductId());

                    System.out.println("Quantity     : "
                            + order.getQuantity());

                    System.out.println();

                    System.out.println("Producing...");

                    Thread.sleep(5000);

                    updateOrderStatus(
                            order.getOrderId(),
                            "COMPLETED"
                    );

                    System.out.println();
                    System.out.println("Production Completed.");

                    System.out.println("Remaining Queue : "
                            + ProductionQueueManager.getQueueSize());

                }

                Thread.sleep(1000);

            }

            catch (InterruptedException e) {

                running = false;

            }

        }

    }

    public void stopWorker() {

        running = false;

    }

    private void updateOrderStatus(int orderId,
                                   String status) {

        String sql = """
                UPDATE production_orders
                SET status = ?
                WHERE order_id = ?
                """;

        try (

                Connection connection =
                        DatabaseConnection.connectDatabase();

                PreparedStatement preparedStatement =
                        connection.prepareStatement(sql)

        ) {

            preparedStatement.setString(1, status);
            preparedStatement.setInt(2, orderId);

            preparedStatement.executeUpdate();

        }

        catch (SQLException e) {

            System.out.println(
                    "Unable to update order status."
            );

            e.printStackTrace();

        }

    }

}