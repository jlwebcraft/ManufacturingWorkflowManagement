package model;

public class RawMaterial {

    private int materialId;
    private String materialName;
    private String unit;
    private double costPerUnit;
    private String status;

    public RawMaterial() {
    }

    public RawMaterial(String materialName,
                       String unit,
                       double costPerUnit) {

        this.materialName = materialName;
        this.unit = unit;
        this.costPerUnit = costPerUnit;

    }

    public RawMaterial(int materialId,
                       String materialName,
                       String unit,
                       double costPerUnit,
                       String status) {

        this.materialId = materialId;
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