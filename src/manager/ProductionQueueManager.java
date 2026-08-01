import java.util.PriorityQueue;

public class ProductionQueueManager {

    private static final PriorityQueue<ProductionOrder> productionQueue =
            new PriorityQueue<>(new ProductionOrderComparator());

    public static void addOrder(ProductionOrder order) {

        productionQueue.offer(order);

        System.out.println();
        System.out.println("==========================================");
        System.out.println("Order Added To Production Queue");
        System.out.println("==========================================");
        System.out.println("Priority : " + order.getPriority());
        System.out.println("Orders Waiting : " + productionQueue.size());

    }

    public static ProductionOrder getNextOrder() {

        return productionQueue.poll();

    }

    public static ProductionOrder peekNextOrder() {

        return productionQueue.peek();

    }

    public static boolean isQueueEmpty() {

        return productionQueue.isEmpty();

    }

    public static int getQueueSize() {

        return productionQueue.size();

    }

    public static void clearQueue() {

        productionQueue.clear();

        System.out.println();
        System.out.println("Production Queue Cleared.");

    }

    public static void displayQueue() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("\tPRODUCTION QUEUE");
        System.out.println("==========================================");

        if (productionQueue.isEmpty()) {

            System.out.println("Queue is empty.");
            return;

        }

        int position = 1;

        for (ProductionOrder order : productionQueue) {

            System.out.println();
            System.out.println("Position : " + position++);
            System.out.println("Product ID : " + order.getProductId());
            System.out.println("Quantity : " + order.getQuantity());
            System.out.println("Machine ID : " + order.getMachineId());
            System.out.println("Priority : " + order.getPriority());

        }

        System.out.println();
        System.out.println("Total Orders : " + productionQueue.size());

    }

}