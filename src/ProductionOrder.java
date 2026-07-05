public class ProductionOrder {

    private int orderId;
    private String orderNumber;
    private int productId;
    private int quantity;
    private int machineId;
    private int createdBy;
    private String status;

    public ProductionOrder() {
    }

    public ProductionOrder(int productId,
                           int quantity,
                           int machineId,
                           int createdBy) {

        this.productId = productId;
        this.quantity = quantity;
        this.machineId = machineId;
        this.createdBy = createdBy;

    }

    public ProductionOrder(int orderId,
                           String orderNumber,
                           int productId,
                           int quantity,
                           int machineId,
                           int createdBy,
                           String status) {

        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.quantity = quantity;
        this.machineId = machineId;
        this.createdBy = createdBy;
        this.status = status;

    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}