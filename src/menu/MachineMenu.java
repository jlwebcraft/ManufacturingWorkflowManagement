package menu;

import manager.MachineManager;

import java.util.Scanner;

public class MachineMenu {

    public static void showMenu() {
        Scanner scanner = util.InputScanner.getScanner();

        while (true) {
            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tMACHINES / RESOURCES");
            System.out.println("==========================================");
            System.out.println("1. Add Machine / Resource");
            System.out.println("2. View All Machines");
            System.out.println("3. View Available Machines");
            System.out.println("4. View Machines Under Maintenance");
            System.out.println("5. Update Machine Status");
            System.out.println("6. Search Machine");
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
                case 1 -> addMachine(scanner);
                case 2 -> MachineManager.viewMachines();
                case 3 -> MachineManager.showAvailableMachines();
                case 4 -> MachineManager.showMaintenanceMachines();
                case 5 -> updateStatus(scanner);
                case 6 -> {
                    System.out.print("Enter machine name/type/status: ");
                    MachineManager.searchMachine(scanner.nextLine().trim());
                }
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid Choice.");
            }
        }
    }

    private static void addMachine(Scanner scanner) {
        System.out.print("Machine Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Machine Type: ");
        String type = scanner.nextLine().trim();

        System.out.print("Daily Capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine());

        if (name.isBlank() || type.isBlank() || capacity <= 0) {
            System.out.println("Invalid machine details.");
            return;
        }

        MachineManager.addMachine(name, type, capacity);
    }

    private static void updateStatus(Scanner scanner) {
        MachineManager.viewMachines();

        System.out.print("Enter Machine ID: ");
        int machineId = Integer.parseInt(scanner.nextLine());

        if (!MachineManager.machineExists(machineId)) {
            System.out.println("Invalid Machine ID.");
            return;
        }

        System.out.println();
        System.out.println("1. AVAILABLE");
        System.out.println("2. BUSY");
        System.out.println("3. MAINTENANCE");
        System.out.println("4. INACTIVE");
        System.out.print("Choice: ");

        int statusChoice = Integer.parseInt(scanner.nextLine());
        String status = null;

        switch (statusChoice) {
            case 1 -> status = "AVAILABLE";
            case 2 -> status = "BUSY";
            case 3 -> status = "MAINTENANCE";
            case 4 -> status = "INACTIVE";
            default -> System.out.println("Invalid Status.");
        }

        if (status != null) {
            MachineManager.updateMachineStatus(machineId, status);
        }
    }
}