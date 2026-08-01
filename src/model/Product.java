public class Product {

    private int productId;
    private int categoryId;
    private String productName;
    private String description;
    private int estimatedProductionHours;
    private double sellingPrice;
    private String status;

    public Product() {
    }

    public Product(int categoryId, String productName, String description,
                   int estimatedProductionHours, double sellingPrice) {

        this.categoryId = categoryId;
        this.productName = productName;
        this.description = description;
        this.estimatedProductionHours = estimatedProductionHours;
        this.sellingPrice = sellingPrice;

    }

    public Product(int productId, int categoryId, String productName,
                   String description, int estimatedProductionHours,
                   double sellingPrice, String status) {

        this.productId = productId;
        this.categoryId = categoryId;
        this.productName = productName;
        this.description = description;
        this.estimatedProductionHours = estimatedProductionHours;
        this.sellingPrice = sellingPrice;
        this.status = status;

    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getEstimatedProductionHours() {
        return estimatedProductionHours;
    }

    public void setEstimatedProductionHours(int estimatedProductionHours) {
        this.estimatedProductionHours = estimatedProductionHours;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}