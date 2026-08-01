package menu;

import manager.ProductionQueueManager;

import java.util.Scanner;

public class ProductionQueueMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

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

            }

            catch (NumberFormatException e) {

                System.out.println("Invalid Input.");
                continue;

            }

            switch (choice) {

                case 1 -> {

                    ProductionQueueManager.displayQueue();

                }

                case 2 -> {

                    if (ProductionQueueManager.isQueueEmpty()) {

                        System.out.println();
                        System.out.println("Queue is empty.");
                        break;

                    }

                    ProductionOrder order =
                            ProductionQueueManager.getNextOrder();

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("NEXT ORDER");
                    System.out.println("==========================================");

                    System.out.println("Order Number : "
                            + order.getOrderNumber());

                    System.out.println("Priority     : "
                            + order.getPriority());

                    System.out.println("Product ID   : "
                            + order.getProductId());

                    System.out.println("Quantity     : "
                            + order.getQuantity());

                    System.out.println("Machine ID   : "
                            + order.getMachineId());

                    System.out.println();
                    System.out.println("Removed From Queue.");

                }

                case 3 -> {

                    ProductionQueueManager.clearQueue();

                }

                case 4 -> {

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("QUEUE STATISTICS");
                    System.out.println("==========================================");

                    System.out.println("Orders Waiting : "
                            + ProductionQueueManager.getQueueSize());

                    if (!ProductionQueueManager.isQueueEmpty()) {

                        ProductionOrder next =
                                ProductionQueueManager.peekNextOrder();

                        System.out.println();
                        System.out.println("Next Order");

                        System.out.println("Order Number : "
                                + next.getOrderNumber());

                        System.out.println("Priority     : "
                                + next.getPriority());

                    }

                }

                case 0 -> {

                    return;

                }

                default -> {

                    System.out.println("Invalid Choice.");

                }

            }

        }

    }

}