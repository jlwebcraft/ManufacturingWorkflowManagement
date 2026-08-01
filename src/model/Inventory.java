public class Inventory {

    private int inventoryId;
    private int materialId;
    private double currentStock;
    private double minimumStock;
    private double maximumStock;

    public Inventory() {
    }

    public Inventory(int materialId,
                     double currentStock,
                     double minimumStock,
                     double maximumStock) {

        this.materialId = materialId;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.maximumStock = maximumStock;

    }

    public Inventory(int inventoryId,
                     int materialId,
                     double currentStock,
                     double minimumStock,
                     double maximumStock) {

        this.inventoryId = inventoryId;
        this.materialId = materialId;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.maximumStock = maximumStock;

    }

    public int getInventoryId() {
        return inventoryId;
    }

    public void setInventoryId(int inventoryId) {
        this.inventoryId = inventoryId;
    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public double getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(double currentStock) {
        this.currentStock = currentStock;
    }

    public double getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(double minimumStock) {
        this.minimumStock = minimumStock;
    }

    public double getMaximumStock() {
        return maximumStock;
    }

    public void setMaximumStock(double maximumStock) {
        this.maximumStock = maximumStock;
    }

}