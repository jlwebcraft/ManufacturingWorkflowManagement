import java.util.Scanner;

public class ProductMenu {

    public static void showMenu() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("==========================================");
            System.out.println("\tPRODUCT MENU");
            System.out.println("==========================================");
            System.out.println("1. Add Product");
            System.out.println("2. View Products");
            System.out.println("3. Update Product");
            System.out.println("4. Delete Product");
            System.out.println("5. Search Product");
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

                case 1:

                    System.out.println();
                    System.out.println("==========================================");
                    System.out.println("\tADD PRODUCT");
                    System.out.println("==========================================");

                    ProductCategoryManager.showCategoryList();

                    System.out.print("Enter Category ID: ");
                    int categoryId = Integer.parseInt(scanner.nextLine());

                    if (!ProductCategoryManager.categoryExists(categoryId)) {

                        System.out.println("Invalid Category ID.");
                        break;

                    }

                    System.out.print("Product Name: ");
                    String productName = scanner.nextLine().trim();

                    System.out.print("Description: ");
                    String description = scanner.nextLine().trim();

                    System.out.print("Estimated Production Hours: ");
                    int hours = Integer.parseInt(scanner.nextLine());

                    System.out.print("Selling Price: ");
                    double price = Double.parseDouble(scanner.nextLine());

                    Product product = new Product(
                            categoryId,
                            productName,
                            description,
                            hours,
                            price
                    );

                    ProductManager.addProduct(product);

                    break;

                case 2:

                    ProductManager.viewProducts();

                    break;

                case 3:

                    ProductManager.showProductList();

                    ProductCategoryManager.showCategoryList();

                    System.out.print("Enter Product ID: ");
                    int productId = Integer.parseInt(scanner.nextLine());

                    if (!ProductManager.productExists(productId)) {

                        System.out.println("Invalid Product ID.");
                        break;

                    }

                    System.out.print("Enter New Category ID: ");
                    int newCategoryId = Integer.parseInt(scanner.nextLine());

                    if (!ProductCategoryManager.categoryExists(newCategoryId)) {

                        System.out.println("Invalid Category ID.");
                        break;

                    }

                    System.out.print("New Product Name: ");
                    String newProductName = scanner.nextLine().trim();

                    System.out.print("New Description: ");
                    String newDescription = scanner.nextLine().trim();

                    System.out.print("Estimated Production Hours: ");
                    int newHours = Integer.parseInt(scanner.nextLine());

                    System.out.print("Selling Price: ");
                    double newPrice = Double.parseDouble(scanner.nextLine());

                    Product updatedProduct = new Product(
                            productId,
                            newCategoryId,
                            newProductName,
                            newDescription,
                            newHours,
                            newPrice,
                            "ACTIVE"
                    );

                    ProductManager.updateProduct(updatedProduct);

                    break;

                case 4:

                    ProductManager.showProductList();

                    System.out.print("Enter Product ID: ");
                    int deleteId = Integer.parseInt(scanner.nextLine());

                    if (!ProductManager.productExists(deleteId)) {

                        System.out.println("Invalid Product ID.");
                        break;

                    }

                    ProductManager.deleteProduct(deleteId);

                    break;

                case 5:

                    System.out.print("Enter Product Name: ");
                    String keyword = scanner.nextLine().trim();

                    ProductManager.searchProduct(keyword);

                    break;

                case 0:

                    return;

                default:

                    System.out.println("Invalid Choice!");

            }
        }
    }
}