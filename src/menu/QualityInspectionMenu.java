package menu;

import manager.QualityInspectionManager;

import java.util.Scanner;

public class QualityInspectionMenu {

    public static void showMenu() {

        Scanner scanner = util.InputScanner.getScanner();

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tQUALITY INSPECTION MENU");
            System.out.println("==========================================");
            System.out.println("1. Record Quality Inspection");
            System.out.println("2. View Quality Inspections");
            System.out.println("3. View Orders Ready For Inspection");
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

                case 1 -> recordInspection(scanner);

                case 2 -> QualityInspectionManager.viewInspections();

                case 3 -> QualityInspectionManager.showOrdersReadyForInspection();

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice!");

            }

        }

    }

    private static void recordInspection(Scanner scanner) {

        QualityInspectionManager.showOrdersReadyForInspection();

        System.out.print("Enter Order ID: ");
        int orderId;

        try {
            orderId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Order ID.");
            return;
        }

        if (!QualityInspectionManager.orderReadyForInspection(orderId)) {
            System.out.println("Invalid Order ID or order is not ready for quality inspection yet.");
            return;
        }

        QualityInspectionManager.showQualityInspectorList();

        System.out.print("Enter Inspector ID: ");
        int inspectorId;

        try {
            inspectorId = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid Inspector ID.");
            return;
        }

        if (!QualityInspectionManager.qualityInspectorExists(inspectorId)) {
            System.out.println("Invalid Quality Inspector ID.");
            return;
        }

        System.out.println();
        System.out.println("==========================================");
        System.out.println("\tSELECT INSPECTION RESULT");
        System.out.println("==========================================");
        System.out.println("1. PASS");
        System.out.println("2. FAIL");
        System.out.println("3. REWORK");
        System.out.print("Choice: ");

        String result;

        try {

            int resultChoice = Integer.parseInt(scanner.nextLine());

            switch (resultChoice) {
                case 1 -> result = "PASS";
                case 2 -> result = "FAIL";
                case 3 -> result = "REWORK";
                default -> {
                    System.out.println("Invalid Result.");
                    return;
                }
            }

        } catch (NumberFormatException e) {
            System.out.println("Invalid Result.");
            return;
        }

        int defectiveQuantity = 0;

        if (!result.equals("PASS")) {

            System.out.print("Enter Defective Quantity: ");

            try {
                defectiveQuantity = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Defective Quantity.");
                return;
            }

            if (defectiveQuantity <= 0) {
                System.out.println("Defective Quantity must be greater than zero.");
                return;
            }

        }

        System.out.print("Remarks: ");
        String remarks = scanner.nextLine().trim();

        if (remarks.isBlank()) {
            remarks = "No remarks.";
        }

        QualityInspectionManager.addInspection(
                orderId,
                inspectorId,
                result,
                defectiveQuantity,
                remarks
        );

    }

}