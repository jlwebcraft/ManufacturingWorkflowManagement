import java.util.Scanner;

public class MainMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==================================================");
            System.out.println("      Manufacturing Workflow Management System");
            System.out.println("==================================================");
            System.out.println();
            System.out.println("1. User Management");
            System.out.println("2. Product Categories");
            System.out.println("3. Products");
            System.out.println("4. Suppliers");
            System.out.println("5. Raw Materials");
            System.out.println("6. Inventory");
            System.out.println("7. Machines");
            System.out.println("8. Production Orders");
            System.out.println("9. Machine Maintenance");
            System.out.println("10. Reports");
            System.out.println("0. Logout");
            System.out.println();

            System.out.print("Enter your choice: ");

            int choice;

            try {

                choice = Integer.parseInt(scanner.nextLine());

            } catch (NumberFormatException e) {

                System.out.println("Invalid input!");
                continue;

            }

            switch (choice) {

                case 1->{
                    UserMenu.showMenu();
                }

                case 2-> {
                    ProductCategoryMenu.showMenu();
                }

                case 3->{
                    ProductMenu.showMenu();
                }

                case 4-> {
                    SupplierMenu.showMenu();
                }

                case 5-> {
                    RawMaterialMenu.showMenu();
                }

                case 6-> {
                    InventoryMenu.showMenu();
                }

                case 7-> {
                    MachineMenu.showMenu();
                }

                case 8-> {
                    ProductionOrderMenu.showMenu();
                }

                case 9-> {
                    MachineMaintenanceMenu.showMenu();
                }

                case 10 -> {
                    ReportMenu.showMenu();
                }

                case 0-> {
                    System.out.println("Logged Out Successfully.");
                    return;
                }

                default-> {
                    System.out.println("Invalid Choice!");
                }

            }

        }

    }

}