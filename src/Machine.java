public class Machine {

    private int machineId;
    private String machineName;
    private String machineType;
    private int dailyCapacity;
    private String capacityUnit;
    private String status;
    private String purchaseDate;
    private String lastServiceDate;

    public Machine() {
    }

    public Machine(String machineName,
                   String machineType,
                   int dailyCapacity,
                   String capacityUnit,
                   String purchaseDate,
                   String lastServiceDate) {

        this.machineName = machineName;
        this.machineType = machineType;
        this.dailyCapacity = dailyCapacity;
        this.capacityUnit = capacityUnit;
        this.purchaseDate = purchaseDate;
        this.lastServiceDate = lastServiceDate;

    }

    public Machine(int machineId,
                   String machineName,
                   String machineType,
                   int dailyCapacity,
                   String capacityUnit,
                   String status,
                   String purchaseDate,
                   String lastServiceDate) {

        this.machineId = machineId;
        this.machineName = machineName;
        this.machineType = machineType;
        this.dailyCapacity = dailyCapacity;
        this.capacityUnit = capacityUnit;
        this.status = status;
        this.purchaseDate = purchaseDate;
        this.lastServiceDate = lastServiceDate;

    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getMachineType() {
        return machineType;
    }

    public void setMachineType(String machineType) {
        this.machineType = machineType;
    }

    public int getDailyCapacity() {
        return dailyCapacity;
    }

    public void setDailyCapacity(int dailyCapacity) {
        this.dailyCapacity = dailyCapacity;
    }

    public String getCapacityUnit() {
        return capacityUnit;
    }

    public void setCapacityUnit(String capacityUnit) {
        this.capacityUnit = capacityUnit;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public String getLastServiceDate() {
        return lastServiceDate;
    }

    public void setLastServiceDate(String lastServiceDate) {
        this.lastServiceDate = lastServiceDate;
    }

}