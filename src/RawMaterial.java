public class RawMaterial {

    private int materialId;
    private int supplierId;
    private String materialName;
    private String unit;
    private double costPerUnit;
    private String status;

    public RawMaterial() {
    }

    public RawMaterial(int supplierId,
                       String materialName,
                       String unit,
                       double costPerUnit) {

        this.supplierId = supplierId;
        this.materialName = materialName;
        this.unit = unit;
        this.costPerUnit = costPerUnit;

    }

    public RawMaterial(int materialId,
                       int supplierId,
                       String materialName,
                       String unit,
                       double costPerUnit,
                       String status) {

        this.materialId = materialId;
        this.supplierId = supplierId;
        this.materialName = materialName;
        this.unit = unit;
        this.costPerUnit = costPerUnit;
        this.status = status;

    }

    public int getMaterialId() {
        return materialId;
    }

    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getCostPerUnit() {
        return costPerUnit;
    }

    public void setCostPerUnit(double costPerUnit) {
        this.costPerUnit = costPerUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}