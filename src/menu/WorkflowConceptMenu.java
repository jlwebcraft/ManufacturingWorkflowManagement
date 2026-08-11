package menu;

import workflow.ManufacturingConceptPractice;

import java.util.Scanner;

public class WorkflowConceptMenu {

    public static void showMenu() {
        Scanner scanner = util.InputScanner.getScanner();

        while (true) {
            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tJAVA / DSA / DBMS PRACTICE");
            System.out.println("==========================================");
            System.out.println("1. Run Practice Examples");
            System.out.println("0. Back");
            System.out.print("Choice: ");

            int choice;

            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input.");
                continue;
            }

            switch (choice) {
                case 1 -> new ManufacturingConceptPractice().runAllExamples();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid Choice.");
            }
        }
    }
}
