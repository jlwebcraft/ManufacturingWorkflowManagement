import java.util.Scanner;

public class InventoryMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tINVENTORY MENU");
            System.out.println("==========================================");

            System.out.println("1. Add Inventory Record");
            System.out.println("2. View Inventory");
            System.out.println("3. Add Stock");
            System.out.println("4. Remove Stock");
            System.out.println("5. Search Inventory");
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

                    RawMaterialManager.showRawMaterialList();

                    System.out.print("Enter Material ID: ");
                    int materialId = Integer.parseInt(scanner.nextLine());

                    if (!RawMaterialManager.rawMaterialExists(materialId)) {

                        System.out.println("Invalid Material ID.");
                        break;

                    }

                    if (InventoryManager.inventoryRecordExists(materialId)) {

                        System.out.println("Inventory record already exists for this material.");
                        break;

                    }

                    System.out.print("Current Stock: ");
                    double currentStock = Double.parseDouble(scanner.nextLine());

                    System.out.print("Minimum Stock: ");
                    double minimumStock = Double.parseDouble(scanner.nextLine());

                    System.out.print("Maximum Stock: ");
                    double maximumStock = Double.parseDouble(scanner.nextLine());

                    if (currentStock < 0) {

                        System.out.println("Current Stock cannot be negative.");
                        break;

                    }

                    if (minimumStock < 0) {
                        System.out.println("Minimum Stock cannot be negative.");
                        break;
                    }

                    if (maximumStock <= 0) {
                        System.out.println("Maximum Stock must be greater than zero.");
                        break;
                    }

                    if (minimumStock > maximumStock) {
                        System.out.println("Minimum Stock cannot be greater than Maximum Stock.");
                        break;
                    }

                    if (currentStock > maximumStock) {
                        System.out.println("Current Stock cannot be greater than Maximum Stock.");
                        break;
                    }

                    Inventory inventory = new Inventory(
                            materialId,
                            currentStock,
                            minimumStock,
                            maximumStock
                    );

                    InventoryManager.addInventoryRecord(inventory);

                }

                case 2 -> {
                    InventoryManager.viewInventory();
                }

                case 3 -> {

                    InventoryManager.showInventoryList();

                    System.out.print("Enter Inventory ID: ");
                    int inventoryId = Integer.parseInt(scanner.nextLine());

                    if (!InventoryManager.inventoryExists(inventoryId)) {

                        System.out.println("Invalid Inventory ID.");
                        break;

                    }

                    System.out.print("Quantity to Add: ");
                    double quantity = Double.parseDouble(scanner.nextLine());

                    InventoryManager.addStock(inventoryId, quantity);

                }

                case 4 -> {

                    InventoryManager.showInventoryList();

                    System.out.print("Enter Inventory ID: ");
                    int inventoryId = Integer.parseInt(scanner.nextLine());

                    if (!InventoryManager.inventoryExists(inventoryId)) {

                        System.out.println("Invalid Inventory ID.");
                        break;

                    }

                    System.out.print("Quantity to Remove: ");
                    double quantity = Double.parseDouble(scanner.nextLine());

                    InventoryManager.removeStock(inventoryId, quantity);

                }

                case 5 -> {

                    System.out.print("Enter Material Name: ");
                    String keyword = scanner.nextLine();

                    InventoryManager.searchInventory(keyword);

                }

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice!");

            }

        }

    }

}