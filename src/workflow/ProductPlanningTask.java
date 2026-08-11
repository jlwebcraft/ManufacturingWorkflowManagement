package workflow;

public class ProductPlanningTask extends AbstractWorkflowTask {

    private final String productName;
    private final int quantity;

    public ProductPlanningTask(String productName, int quantity) {
        super("Product Planning");
        this.productName = productName;
        this.quantity = quantity;
    }

    @Override
    public void executeStep() {
        printHeader();
        System.out.println("Product       : " + productName);
        System.out.println("Quantity      : " + quantity);
    }
}