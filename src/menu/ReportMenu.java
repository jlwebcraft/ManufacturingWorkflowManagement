package menu;

import manager.ReportManager;

import java.util.Scanner;

public class ReportMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tREPORTS");
            System.out.println("==========================================");

            System.out.println("1. Inventory Report");
            System.out.println("2. Low Stock Report");
            System.out.println("3. Production Order Report");
            System.out.println("4. Machine Maintenance Report");
            System.out.println("5. Export Monthly Report");
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

                case 1 -> ReportManager.inventoryReport();

                case 2 -> ReportManager.lowStockReport();

                case 3 -> ReportManager.productionOrderReport();

                case 4 -> ReportManager.machineMaintenanceReport();

                case 5 -> {

                    System.out.print("Enter Month (1-12): ");
                    int month = Integer.parseInt(scanner.nextLine());

                    if (month < 1 || month > 12) {

                        System.out.println("Invalid Month.");
                        break;

                    }

                    System.out.print("Enter Year: ");
                    int year = Integer.parseInt(scanner.nextLine());

                    Thread reportThread =
                            new Thread(
                                    new ReportExportTask(month, year)
                            );

                    reportThread.start();

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("Report export started in background...");
                    System.out.println("You may continue using the system.");
                    System.out.println("==========================================");

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