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