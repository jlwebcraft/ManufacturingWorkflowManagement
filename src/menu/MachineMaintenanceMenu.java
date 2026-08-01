import java.util.Scanner;

public class MachineMaintenanceMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tMACHINE MAINTENANCE");
            System.out.println("==========================================");

            System.out.println("1. Add Maintenance Record");
            System.out.println("2. View Maintenance History");
            System.out.println("3. Search Maintenance History");
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

                    MachineManager.showMachineList();

                    System.out.print("Enter Machine ID: ");
                    int machineId = Integer.parseInt(scanner.nextLine());

                    if (!MachineManager.machineExists(machineId)) {

                        System.out.println("Invalid Machine ID.");
                        break;

                    }

                    System.out.print("Maintenance Date (YYYY-MM-DD): ");
                    String maintenanceDate = scanner.nextLine().trim();

                    System.out.print("Maintenance Type: ");
                    String maintenanceType = scanner.nextLine().trim();

                    System.out.print("Technician: ");
                    String technician = scanner.nextLine().trim();

                    System.out.print("Cost: ");
                    double cost = Double.parseDouble(scanner.nextLine());

                    if (cost < 0) {

                        System.out.println("Cost cannot be negative.");
                        break;

                    }

                    System.out.print("Remarks: ");
                    String remarks = scanner.nextLine().trim();

                    System.out.print("Next Service Date (YYYY-MM-DD): ");
                    String nextServiceDate = scanner.nextLine().trim();

                    MachineMaintenance maintenance =
                            new MachineMaintenance(
                                    machineId,
                                    maintenanceDate,
                                    maintenanceType,
                                    technician,
                                    cost,
                                    remarks,
                                    nextServiceDate
                            );

                    MachineMaintenanceManager.addMaintenanceRecord(maintenance);

                }

                case 2 -> {

                    MachineMaintenanceManager.viewMaintenanceHistory();

                }

                case 3 -> {

                    MachineManager.showMachineList();

                    System.out.print("Enter Machine ID: ");
                    int machineId = Integer.parseInt(scanner.nextLine());

                    if (!MachineManager.machineExists(machineId)) {

                        System.out.println("Invalid Machine ID.");
                        break;

                    }

                    MachineMaintenanceManager.searchMaintenanceHistory(machineId);

                }

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