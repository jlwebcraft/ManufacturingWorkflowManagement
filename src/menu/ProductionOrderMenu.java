package menu;

import manager.ProductionOrderManager;
import manager.ProductManager;
import manager.MachineManager;
import model.ProductionOrder;
import manager.UserManager;

import java.util.Scanner;

public class ProductionOrderMenu {

    public static void showMenu() {

        Scanner scanner = util.InputScanner.getScanner();

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tPRODUCTION ORDER MENU");
            System.out.println("==========================================");

            System.out.println("1. Create Production Order");
            System.out.println("2. View Production Orders");
            System.out.println("3. Update Manufacturing Status");
            System.out.println("4. Cancel Production Order");
            System.out.println("5. Search Production Order");
            System.out.println("0. Back");

            System.out.print("Choice: ");

            int choice;

            try {

                choice = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Invalid Input!");
                continue;

            }

            switch (choice) {

                case 1 -> {

                    ProductManager.showProductList();

                    System.out.print("Enter Product ID: ");
                    int productId = Integer.parseInt(scanner.nextLine());

                    if (!ProductManager.productExists(productId)) {

                        System.out.println("Invalid Product ID.");
                        break;

                    }

                    System.out.print("Enter Quantity: ");
                    int quantity = Integer.parseInt(scanner.nextLine());

                    if (quantity <= 0) {

                        System.out.println("Quantity must be greater than zero.");
                        break;

                    }

                    if (!ProductionOrderManager.hasEnoughMaterials(productId, quantity)) {

                        break;

                    }

                    MachineManager.showAvailableMachines();

                    System.out.print("Enter Machine ID: ");
                    int machineId = Integer.parseInt(scanner.nextLine());

                    if (!MachineManager.machineAvailable(machineId)) {

                        System.out.println("Invalid Machine ID or machine is not available.");
                        break;

                    }

                    UserManager.showUserList();

                    System.out.print("Enter User ID: ");
                    int userId = Integer.parseInt(scanner.nextLine());

                    if (!UserManager.userExists(userId)) {

                        System.out.println("Invalid User ID.");
                        break;

                    }

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("\tSELECT PRIORITY");
                    System.out.println("==========================================");
                    System.out.println("1. LOW");
                    System.out.println("2. MEDIUM");
                    System.out.println("3. HIGH");

                    System.out.print("Choice: ");

                    int priorityChoice = Integer.parseInt(scanner.nextLine());

                    String priority = null;

                    switch (priorityChoice) {

                        case 1:
                            priority = "LOW";
                            break;

                        case 2:
                            priority = "MEDIUM";
                            break;

                        case 3:
                            priority = "HIGH";
                            break;

                        default:
                            System.out.println("Invalid Priority.");
                            break;

                    }

                    if (priority == null) {

                        break;

                    }

                    ProductionOrder order = new ProductionOrder(
                            productId,
                            machineId,
                            quantity,
                            userId,
                            priority
                    );

                    ProductionOrderManager.addProductionOrder(order);

                }
                case 2 -> {

                    ProductionOrderManager.viewProductionOrders();

                }

                case 3 -> {

                    ProductionOrderManager.showProductionOrderList();

                    System.out.print("Enter Order ID: ");
                    int orderId = Integer.parseInt(scanner.nextLine());

                    if (!ProductionOrderManager.productionOrderExists(orderId)) {

                        System.out.println("Invalid Order ID.");
                        break;

                    }

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("\tUPDATE MANUFACTURING STATUS");
                    System.out.println("==========================================");
                    System.out.println("1. Start Manufacturing");
                    System.out.println("2. Send To Quality Check");

                    System.out.print("Choice: ");

                    int statusChoice = Integer.parseInt(scanner.nextLine());

                    String status = null;

                    switch (statusChoice) {

                        case 1:
                            status = "IN_PROGRESS";
                            break;

                        case 2:
                            status = "QUALITY_CHECK";
                            break;

                        default:
                            System.out.println("Invalid Choice.");
                            break;

                    }

                    if (status == null) {

                        break;

                    }

                    ProductionOrderManager.updateOrderStatus(orderId, status);

                }

                case 4 -> {

                    ProductionOrderManager.showProductionOrderList();

                    System.out.print("Enter Order ID: ");
                    int orderId = Integer.parseInt(scanner.nextLine());

                    if (!ProductionOrderManager.productionOrderExists(orderId)) {

                        System.out.println("Invalid Order ID.");
                        break;

                    }

                    ProductionOrderManager.cancelProductionOrder(orderId);

                }

                case 5 -> {

                    System.out.print("Enter Order Number: ");
                    String keyword = scanner.nextLine().trim();

                    ProductionOrderManager.searchProductionOrder(keyword);

                }

                case 0 -> {

                    return;

                }

                default -> {

                    System.out.println("Invalid Choice!");

                }

            }

        }

    }

}
