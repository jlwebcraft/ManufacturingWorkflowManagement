import java.util.Scanner;

public class ProductCategoryMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {

            System.out.println();
            System.out.println("========== PRODUCT CATEGORY ==========");
            System.out.println("1. Add Category");
            System.out.println("2. View Categories");
            System.out.println("3. Update Categories");
            System.out.println("4. Delete Categories");
            System.out.println("5. Search Categories");
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
                    System.out.print("Category Name: ");
                    String name = scanner.nextLine().trim();

                    System.out.print("Description: ");
                    String description = scanner.nextLine().trim();

                    ProductCategory category =
                            new ProductCategory(name, description);

                    ProductCategoryManager.addCategory(category);
                }

                case 2->{
                    ProductCategoryManager.viewCategories();
                }

                case 3-> {
                    System.out.print("Category ID: ");
                    int updateId = Integer.parseInt(scanner.nextLine());

                    System.out.print("New Name: ");
                    String newName = scanner.nextLine().trim();

                    System.out.print("New Description: ");
                    String newDescription = scanner.nextLine().trim();

                    ProductCategoryManager.updateCategory(
                            updateId,
                            newName,
                            newDescription);
                }

                case 4-> {
                    System.out.print("Category ID: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());

                    ProductCategoryManager.deleteCategory(deleteId);
                }

                case 5-> {

                    System.out.print("Search: ");
                    String keyword = scanner.nextLine().trim();

                    ProductCategoryManager.searchCategory(keyword);
                }

                case 0-> {
                    return;
                }

                default->{
                    System.out.println("Invalid Choice!");
                }

            }

        }

    }

}