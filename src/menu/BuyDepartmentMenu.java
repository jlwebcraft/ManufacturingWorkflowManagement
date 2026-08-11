package menu;

import manager.InventoryManager;
import manager.RawMaterialManager;

import java.util.Scanner;

public class BuyDepartmentMenu {

    public static void showMenu() {

        Scanner scanner = util.InputScanner.getScanner();

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tBUY DEPARTMENT");
            System.out.println("==========================================");

            System.out.println("1. Add Raw Material Stock");
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

                case 1 -> placeRawMaterialOrder(scanner);

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice!");

            }

        }

    }

    private static void placeRawMaterialOrder(Scanner scanner) {

        RawMaterialManager.showRawMaterialList();

        System.out.print("Enter Raw Material ID: ");
        int materialId = Integer.parseInt(scanner.nextLine());

        if (!RawMaterialManager.rawMaterialExists(materialId)) {

            System.out.println("Invalid Raw Material ID.");
            return;

        }

        System.out.print("Enter Quantity to Order: ");
        double quantity = Double.parseDouble(scanner.nextLine());

        if (quantity <= 0) {

            System.out.println("Quantity must be greater than zero.");
            return;

        }

        InventoryManager.orderRawMaterial(materialId, quantity);

    }

}