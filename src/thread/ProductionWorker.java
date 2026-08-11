package thread;

import manager.ProductionQueueManager;
import model.ProductionOrder;

public class ProductionWorker implements Runnable {

    private boolean running = true;

    @Override
    public void run() {

        while (running) {

            try {

                ProductionOrder order = ProductionQueueManager.getNextOrder();

                if (order != null) {

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("PRODUCTION STARTED");
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

                    ProductionQueueManager.completeOrder(order.getOrderId());

                    System.out.println();
                    System.out.println("Production Completed.");
                    System.out.println("Remaining Queue : "
                            + ProductionQueueManager.getQueueSize());

                }

                Thread.sleep(1000);

            } catch (InterruptedException e) {

                running = false;

            }

        }

    }

    public void stopWorker() {

        running = false;

    }

}