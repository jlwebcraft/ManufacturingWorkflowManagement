import java.util.Scanner;

public class SupplierMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tSUPPLIER MENU");
            System.out.println("==========================================");

            System.out.println("1. Add Supplier");
            System.out.println("2. View Suppliers");
            System.out.println("3. Update Supplier");
            System.out.println("4. Delete Supplier");
            System.out.println("5. Search Supplier");
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

                case 1-> {
                    System.out.print("Supplier Name: ");
                    String supplierName = scanner.nextLine().trim();

                    System.out.print("Contact Person: ");
                    String contactPerson = scanner.nextLine().trim();

                    System.out.print("Phone: ");
                    String phone = scanner.nextLine().trim();

                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();

                    System.out.print("Address: ");
                    String address = scanner.nextLine().trim();

                    Supplier supplier = new Supplier(
                            supplierName,
                            contactPerson,
                            phone,
                            email,
                            address
                    );

                    SupplierManager.addSupplier(supplier);

                }

                case 2-> {
                    SupplierManager.viewSuppliers();
                }

                case 3-> {
                    SupplierManager.viewSuppliers();

                    System.out.print("Enter Supplier ID: ");

                    int updateId = Integer.parseInt(scanner.nextLine());

                    if (!SupplierManager.supplierExists(updateId)) {

                        System.out.println("Invalid Supplier ID.");
                        break;

                    }

                    System.out.print("Supplier Name: ");
                    String supplierName = scanner.nextLine().trim();

                    System.out.print("Contact Person: ");
                    String contactPerson = scanner.nextLine().trim();

                    System.out.print("Phone: ");
                    String phone = scanner.nextLine().trim();

                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();

                    System.out.print("Address: ");
                    String address = scanner.nextLine().trim();

                    Supplier updatedSupplier = new Supplier(
                            updateId,
                            supplierName,
                            contactPerson,
                            phone,
                            email,
                            address,
                            "ACTIVE"
                    );

                    SupplierManager.updateSupplier(updatedSupplier);
                }

                case 4->{
                    SupplierManager.viewSuppliers();

                    System.out.print("Enter Supplier ID: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());

                    if (!SupplierManager.supplierExists(deleteId)) {

                        System.out.println("Invalid Supplier ID.");
                        break;

                    }

                    SupplierManager.deleteSupplier(deleteId);
                }

                case 5-> {
                    System.out.print("Enter Supplier Name: ");
                    String keyword = scanner.nextLine().trim();

                    SupplierManager.searchSupplier(keyword);
                }

                case 0->{
                    return;
                }

                default->{
                    System.out.println("Invalid Choice!");
                }

            }

        }

    }

}