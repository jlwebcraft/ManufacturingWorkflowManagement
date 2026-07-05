public class MachineMaintenance {

    private int maintenanceId;
    private int machineId;
    private String maintenanceDate;
    private String maintenanceType;
    private String technician;
    private double cost;
    private String remarks;
    private String nextServiceDate;

    public MachineMaintenance() {
    }

    public MachineMaintenance(int machineId,
                              String maintenanceDate,
                              String maintenanceType,
                              String technician,
                              double cost,
                              String remarks,
                              String nextServiceDate) {

        this.machineId = machineId;
        this.maintenanceDate = maintenanceDate;
        this.maintenanceType = maintenanceType;
        this.technician = technician;
        this.cost = cost;
        this.remarks = remarks;
        this.nextServiceDate = nextServiceDate;

    }

    public MachineMaintenance(int maintenanceId,
                              int machineId,
                              String maintenanceDate,
                              String maintenanceType,
                              String technician,
                              double cost,
                              String remarks,
                              String nextServiceDate) {

        this.maintenanceId = maintenanceId;
        this.machineId = machineId;
        this.maintenanceDate = maintenanceDate;
        this.maintenanceType = maintenanceType;
        this.technician = technician;
        this.cost = cost;
        this.remarks = remarks;
        this.nextServiceDate = nextServiceDate;

    }

    public int getMaintenanceId() {
        return maintenanceId;
    }

    public void setMaintenanceId(int maintenanceId) {
        this.maintenanceId = maintenanceId;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getMaintenanceDate() {
        return maintenanceDate;
    }

    public void setMaintenanceDate(String maintenanceDate) {
        this.maintenanceDate = maintenanceDate;
    }

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getTechnician() {
        return technician;
    }

    public void setTechnician(String technician) {
        this.technician = technician;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getNextServiceDate() {
        return nextServiceDate;
    }

    public void setNextServiceDate(String nextServiceDate) {
        this.nextServiceDate = nextServiceDate;
    }

}