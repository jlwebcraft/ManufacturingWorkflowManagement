package model;

public class ProductionOrder {

    private int orderId;
    private String orderNumber;
    private int productId;
    private int machineId;
    private int quantity;
    private int createdBy;
    private String priority;
    private String status;

    public ProductionOrder() {
    }

    public ProductionOrder(int productId,
                           int machineId,
                           int quantity,
                           int createdBy,
                           String priority) {

        this.productId = productId;
        this.machineId = machineId;
        this.quantity = quantity;
        this.createdBy = createdBy;
        this.priority = priority;

    }

    public ProductionOrder(int orderId,
                           String orderNumber,
                           int productId,
                           int machineId,
                           int quantity,
                           int createdBy,
                           String priority,
                           String status) {

        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.productId = productId;
        this.machineId = machineId;
        this.quantity = quantity;
        this.createdBy = createdBy;
        this.priority = priority;
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

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}