import java.util.Scanner;

public class MachineMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tMACHINE MENU");
            System.out.println("==========================================");

            System.out.println("1. Add Machine");
            System.out.println("2. View Machines");
            System.out.println("3. Update Machine");
            System.out.println("4. Delete Machine");
            System.out.println("5. Search Machine");
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

                    System.out.print("Machine Name: ");
                    String machineName = scanner.nextLine().trim();

                    System.out.print("Machine Type: ");
                    String machineType = scanner.nextLine().trim();

                    System.out.print("Daily Capacity: ");
                    int dailyCapacity = Integer.parseInt(scanner.nextLine());

                    System.out.print("Capacity Unit: ");
                    String capacityUnit = scanner.nextLine().trim();

                    System.out.print("Purchase Date (YYYY-MM-DD): ");
                    String purchaseDate = scanner.nextLine().trim();

                    System.out.print("Last Service Date (YYYY-MM-DD): ");
                    String lastServiceDate = scanner.nextLine().trim();

                    Machine machine = new Machine(
                            machineName,
                            machineType,
                            dailyCapacity,
                            capacityUnit,
                            purchaseDate,
                            lastServiceDate
                    );

                    MachineManager.addMachine(machine);

                }

                case 2 ->{
                        MachineManager.viewMachines();
                }

                case 3 -> {

                    MachineManager.showMachineList();

                    System.out.print("Enter Machine ID: ");
                    int machineId = Integer.parseInt(scanner.nextLine());

                    if (!MachineManager.machineExists(machineId)) {

                        System.out.println("Invalid Machine ID.");
                        break;

                    }

                    System.out.print("Machine Name: ");
                    String machineName = scanner.nextLine().trim();

                    System.out.print("Machine Type: ");
                    String machineType = scanner.nextLine().trim();

                    System.out.print("Daily Capacity: ");
                    int dailyCapacity = Integer.parseInt(scanner.nextLine());

                    System.out.print("Capacity Unit: ");
                    String capacityUnit = scanner.nextLine().trim();

                    System.out.print("Purchase Date (YYYY-MM-DD): ");
                    String purchaseDate = scanner.nextLine().trim();

                    System.out.print("Last Service Date (YYYY-MM-DD): ");
                    String lastServiceDate = scanner.nextLine().trim();

                    Machine machine = new Machine(
                            machineId,
                            machineName,
                            machineType,
                            dailyCapacity,
                            capacityUnit,
                            "AVAILABLE",
                            purchaseDate,
                            lastServiceDate
                    );

                    MachineManager.updateMachine(machine);

                }

                case 4 -> {

                    MachineManager.showMachineList();

                    System.out.print("Enter Machine ID: ");
                    int machineId = Integer.parseInt(scanner.nextLine());

                    if (!MachineManager.machineExists(machineId)) {

                        System.out.println("Invalid Machine ID.");
                        break;

                    }

                    MachineManager.deleteMachine(machineId);

                }

                case 5 -> {

                    System.out.print("Enter Machine Name: ");
                    String keyword = scanner.nextLine().trim();

                    MachineManager.searchMachine(keyword);

                }

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice!");

            }

        }

    }

}