import java.util.Scanner;

public class UserMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tUSER MENU");
            System.out.println("==========================================");

            System.out.println("1. Add User");
            System.out.println("2. View Users");
            System.out.println("3. Update User");
            System.out.println("4. Delete User");
            System.out.println("5. Search User");
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

                    RoleManager.showRoles();

                    System.out.print("Enter Role ID: ");
                    int roleId = Integer.parseInt(scanner.nextLine());

                    if (!RoleManager.roleExists(roleId)) {

                        System.out.println("Invalid Role ID.");
                        break;

                    }

                    System.out.print("Name: ");
                    String name = scanner.nextLine().trim();

                    System.out.print("Username: ");
                    String username = scanner.nextLine().trim();

                    if (UserManager.usernameExists(username)) {
                        System.out.println("Username already exists.");
                        break;
                    }

                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();

                    if (UserManager.emailExists(email)) {
                        System.out.println("Email already exists.");
                        break;
                    }

                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();

                    System.out.print("Phone: ");
                    String phone = scanner.nextLine().trim();

                    User user = new User(
                            roleId,
                            name,
                            username,
                            email,
                            password,
                            phone
                    );

                    UserManager.addUser(user);

                }

                case 2 -> {
                        UserManager.viewUsers();
                }

                case 3 -> {

                    UserManager.viewUsers();

                    RoleManager.showRoles();

                    System.out.print("Enter User ID: ");
                    int userId = Integer.parseInt(scanner.nextLine());

                    if (!UserManager.userExists(userId)) {

                        System.out.println("Invalid User ID.");
                        break;

                    }

                    System.out.print("Enter Role ID: ");
                    int roleId = Integer.parseInt(scanner.nextLine());

                    if (!RoleManager.roleExists(roleId)) {

                        System.out.println("Invalid Role ID.");
                        break;

                    }

                    System.out.print("Name: ");
                    String name = scanner.nextLine().trim();

                    System.out.print("Username: ");
                    String username = scanner.nextLine().trim();

                    System.out.print("Email: ");
                    String email = scanner.nextLine().trim();

                    System.out.print("Password: ");
                    String password = scanner.nextLine().trim();

                    System.out.print("Phone: ");
                    String phone = scanner.nextLine().trim();

                    User user = new User(
                            userId,
                            roleId,
                            name,
                            username,
                            email,
                            password,
                            phone,
                            "ACTIVE"
                    );

                    UserManager.updateUser(user);

                }

                case 4 -> {

                    UserManager.viewUsers();

                    System.out.print("Enter User ID: ");
                    int userId = Integer.parseInt(scanner.nextLine());

                    if (!UserManager.userExists(userId)) {

                        System.out.println("Invalid User ID.");
                        break;

                    }
                    UserManager.deleteUser(userId);
                }

                case 5 -> {
                    System.out.print("Enter Name: ");
                    String keyword = scanner.nextLine().trim();

                    UserManager.searchUser(keyword);
                }

                case 0 -> {
                    return;
                }

                default -> System.out.println("Invalid Choice!");

            }

        }

    }

}