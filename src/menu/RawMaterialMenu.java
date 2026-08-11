package menu;

import manager.RawMaterialManager;
import model.RawMaterial;

import java.util.Scanner;

public class RawMaterialMenu {

    public static void showMenu() {

        Scanner scanner = util.InputScanner.getScanner();

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tRAW MATERIAL MENU");
            System.out.println("==========================================");

            System.out.println("1. View Raw Materials");
            System.out.println("2. Update Raw Material");
            System.out.println("3. Delete Raw Material");
            System.out.println("4. Search Raw Material");
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

                case 1 -> RawMaterialManager.viewRawMaterials();

                case 2 -> {

                    RawMaterialManager.showRawMaterialList();

                    System.out.print("Enter Material ID: ");
                    int materialId = Integer.parseInt(scanner.nextLine());

                    if (!RawMaterialManager.rawMaterialExists(materialId)) {

                        System.out.println("Invalid Material ID.");
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
                            materialName,
                            unit,
                            cost,
                            "ACTIVE"
                    );

                    RawMaterialManager.updateRawMaterial(rawMaterial);

                }

                case 3 -> {
                    RawMaterialManager.showRawMaterialList();

                    System.out.print("Enter Material ID: ");
                    int materialId = Integer.parseInt(scanner.nextLine());

                    if (!RawMaterialManager.rawMaterialExists(materialId)) {

                        System.out.println("Invalid Material ID.");
                        break;

                    }

                    RawMaterialManager.deleteRawMaterial(materialId);

                }

                case 4 -> {
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