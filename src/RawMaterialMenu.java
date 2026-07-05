import java.util.Scanner;

public class RawMaterialMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tRAW MATERIAL MENU");
            System.out.println("==========================================");

            System.out.println("1. Add Raw Material");
            System.out.println("2. View Raw Materials");
            System.out.println("3. Update Raw Material");
            System.out.println("4. Delete Raw Material");
            System.out.println("5. Search Raw Material");
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

                    SupplierManager.showSupplierList();

                    System.out.print("Enter Supplier ID: ");
                    int supplierId = Integer.parseInt(scanner.nextLine());

                    if (!SupplierManager.supplierExists(supplierId)) {

                        System.out.println("Invalid Supplier ID.");
                        break;

                    }

                    System.out.print("Material Name: ");
                    String materialName = scanner.nextLine().trim();

                    System.out.print("Unit (Kg, Litre, Piece): ");
                    String unit = scanner.nextLine().trim();

                    System.out.print("Cost Per Unit: ");
                    double cost = Double.parseDouble(scanner.nextLine());

                    RawMaterial rawMaterial = new RawMaterial(
                            supplierId,
                            materialName,
                            unit,
                            cost
                    );

                    RawMaterialManager.addRawMaterial(rawMaterial);

                }

                case 2 -> {
                    RawMaterialManager.viewRawMaterials();
                }
                case 3 -> {

                    RawMaterialManager.viewRawMaterials();

                    SupplierManager.showSupplierList();

                    System.out.print("Enter Material ID: ");
                    int materialId = Integer.parseInt(scanner.nextLine());

                    if (!RawMaterialManager.rawMaterialExists(materialId)) {

                        System.out.println("Invalid Material ID.");
                        break;

                    }

                    System.out.print("Enter Supplier ID: ");
                    int supplierId = Integer.parseInt(scanner.nextLine());

                    if (!SupplierManager.supplierExists(supplierId)) {

                        System.out.println("Invalid Supplier ID.");
                        break;

                    }

                    System.out.print("Material Name: ");
                    String materialName = scanner.nextLine().trim();

                    System.out.print("Unit: ");
                    String unit = scanner.nextLine().trim();

                    System.out.print("Cost Per Unit: ");
                    double cost = Double.parseDouble(scanner.nextLine());

                    RawMaterial rawMaterial = new RawMaterial(
                            materialId,
                            supplierId,
                            materialName,
                            unit,
                            cost,
                            "ACTIVE"
                    );

                    RawMaterialManager.updateRawMaterial(rawMaterial);

                }
                case 4 -> {
                    RawMaterialManager.viewRawMaterials();

                    System.out.print("Enter Material ID: ");
                    int materialId = Integer.parseInt(scanner.nextLine());

                    if (!RawMaterialManager.rawMaterialExists(materialId)) {

                        System.out.println("Invalid Material ID.");
                        break;

                    }

                    RawMaterialManager.deleteRawMaterial(materialId);

                }
                case 5 -> {
                    System.out.print("Enter Material Name: ");
                    String keyword = scanner.nextLine().trim();

                    RawMaterialManager.searchRawMaterial(keyword);

                }

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice!");

            }

        }

    }

}