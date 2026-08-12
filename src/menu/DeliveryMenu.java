package menu;

import manager.DeliveryManager;
import manager.UserManager;

import java.util.Scanner;

public class DeliveryMenu {

    public static void showMenu() {
        Scanner scanner = util.InputScanner.getScanner();

        while (true) {
            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tDELIVERY MENU");
            System.out.println("==========================================");
            System.out.println("1. View Orders Ready For Delivery");
            System.out.println("2. Deliver Order");
            System.out.println("3. View Delivery Records");
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
                case 1 -> DeliveryManager.showDeliverableOrders();
                case 2 -> deliverOrder(scanner);
                case 3 -> DeliveryManager.viewDeliveries();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid Choice.");
            }
        }
    }

    private static void deliverOrder(Scanner scanner) {
        DeliveryManager.showDeliverableOrders();

        System.out.print("Enter Order ID: ");
        int orderId;
        try {
            orderId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Order ID.");
            return;
        }

        if (!DeliveryManager.orderReadyForDelivery(orderId)) {
            System.out.println("Invalid Order ID or order is not ready for delivery.");
            return;
        }

        UserManager.showUserList();

        System.out.print("Enter Delivered By User ID: ");
        int deliveredBy;
        try {
            deliveredBy = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid User ID.");
            return;
        }

        if (!UserManager.userExists(deliveredBy)) {
            System.out.println("Invalid User ID.");
            return;
        }

        System.out.print("Delivered To: ");
        String deliveredTo = scanner.nextLine().trim();

        if (deliveredTo.isBlank()) {
            System.out.println("Delivered To cannot be empty.");
            return;
        }

        System.out.print("Delivery Address: ");
        String deliveryAddress = scanner.nextLine().trim();

        if (deliveryAddress.isBlank()) {
            deliveryAddress = "Not provided";
        }

        System.out.print("Remarks: ");
        String remarks = scanner.nextLine().trim();

        if (remarks.isBlank()) {
            remarks = "Delivered successfully.";
        }

        DeliveryManager.deliverOrder(orderId, deliveredBy, deliveredTo, deliveryAddress, remarks);
    }
}
