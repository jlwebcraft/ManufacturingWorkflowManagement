package menu;

import manager.ProductionQueueManager;
import model.*;
import manager.*;

import java.util.Scanner;

public class ProductionQueueMenu {

    public static void showMenu() {

        Scanner scanner = util.InputScanner.getScanner();

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tPRODUCTION QUEUE");
            System.out.println("==========================================");

            System.out.println("1. View Queue");
            System.out.println("2. Process Next Order");
            System.out.println("3. Clear Queue");
            System.out.println("4. Queue Statistics");
            System.out.println("0. Back");

            System.out.print("Choice: ");

            int choice;

            try {

                choice = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Invalid Input.");
                continue;

            }

            switch (choice) {

                case 1 -> ProductionQueueManager.displayQueue();

                case 2 -> processNextOrder();

                case 3 -> ProductionQueueManager.clearQueue();

                case 4 -> showQueueStatistics();

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice.");

            }

        }

    }

    private static void processNextOrder() {

        ProductionOrder order = ProductionQueueManager.getNextOrder();

        if (order == null) {

            System.out.println();
            System.out.println("No pending production order has an available machine.");
            return;

        }

        System.out.println();
        System.out.println("==========================================");
        System.out.println("PRODUCTION STARTED");
        System.out.println("==========================================");
        System.out.println("Order Number : " + order.getOrderNumber());
        System.out.println("Priority     : " + order.getPriority());
        System.out.println("Product ID   : " + order.getProductId());
        System.out.println("Quantity     : " + order.getQuantity());
        System.out.println("Status       : IN_PROGRESS");

        ProductionQueueManager.completeOrder(order.getOrderId());

        System.out.println();
        System.out.println("==========================================");
        System.out.println("MANUFACTURING FINISHED");
        System.out.println("==========================================");
        System.out.println("Order Number : " + order.getOrderNumber());
        System.out.println("Status       : QUALITY_CHECK");
        System.out.println("Ready For Quality Inspection.");
        System.out.println("Remaining Queue : " + ProductionQueueManager.getQueueSize());

    }

    private static void showQueueStatistics() {

        System.out.println();
        System.out.println("==========================================");
        System.out.println("QUEUE STATISTICS");
        System.out.println("==========================================");
        System.out.println("Orders Waiting : " + ProductionQueueManager.getQueueSize());

        ProductionOrder next = ProductionQueueManager.peekNextOrder();

        if (next != null) {

            System.out.println();
            System.out.println("Next Order");
            System.out.println("Order Number : " + next.getOrderNumber());
            System.out.println("Priority     : " + next.getPriority());

        }

    }

}
