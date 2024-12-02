public class MobileVehicleInstance {
    private String vehicleRegistration;
    private String employeeSpecifiedVehicleClass;
    private double vehicleSpeedMPS;
    private String tollPlaza;
    private int laneNumber;
    private String directionId;
    // Can be DA (for Delhi-Agra direction) or AD (for Agra-Delhi direction); proxy for destination location
    private long transactionTimeSeconds;
    private long precedingVehicleTransactionTimeSeconds;
    // Preceding vehicle is one that shares the same plaza name and lane number combination
    private int rankInQueue;
    // Also flags whether queuing occurs
    private double timeSpentAtPlazaInSeconds;
    private int distanceFromDestinationAtStipulatedTime;
    private double ascribedXCoordinateAtStipulatedTime;
    private double ascribedYCoordinateAtStipulatedTime;

    MobileVehicleInstance(String vehicleRegistration, String employeeSpecifiedVehicleClass, double vehicleSpeedMPS,
                          String tollPlaza, int laneNumber, String directionId, long transactionTimeSeconds,
                          long precedingVehicleTransactionTimeSeconds, int rankInQueue,
                          double timeSpentAtPlazaInSeconds, int distanceFromDestinationAtStipulatedTime,
                          double ascribedXCoordinateAtStipulatedTime, double ascribedYCoordinateAtStipulatedTime) {
        this.vehicleRegistration = vehicleRegistration;
        this.employeeSpecifiedVehicleClass = employeeSpecifiedVehicleClass;
        this.vehicleSpeedMPS = vehicleSpeedMPS;
        this.tollPlaza = tollPlaza;
        this.laneNumber = laneNumber;
        this.directionId = directionId;
        this.transactionTimeSeconds = transactionTimeSeconds;
        this.precedingVehicleTransactionTimeSeconds = precedingVehicleTransactionTimeSeconds;
        this.rankInQueue = rankInQueue;
        this.timeSpentAtPlazaInSeconds = timeSpentAtPlazaInSeconds;
        this.distanceFromDestinationAtStipulatedTime = distanceFromDestinationAtStipulatedTime;
        this.ascribedXCoordinateAtStipulatedTime = ascribedXCoordinateAtStipulatedTime;
        this.ascribedYCoordinateAtStipulatedTime = ascribedYCoordinateAtStipulatedTime;
    }

    public void setDistanceFromDestinationAtStipulatedTime(int distanceFromDestinationAtStipulatedTime) {
        this.distanceFromDestinationAtStipulatedTime = distanceFromDestinationAtStipulatedTime;
    }

    public void setAscribedXCoordinateAtStipulatedTime(double xCoordinateAtStipulatedTime) {
        this.ascribedXCoordinateAtStipulatedTime = xCoordinateAtStipulatedTime;
    }

    public void setAscribedYCoordinateAtStipulatedTime(double yCoordinateAtStipulatedTime) {
        this.ascribedYCoordinateAtStipulatedTime = yCoordinateAtStipulatedTime;
    }

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public String getEmployeeSpecifiedVehicleClass() {
        return employeeSpecifiedVehicleClass;
    }

    public double getVehicleSpeedMPS() {
        return this.vehicleSpeedMPS;
    }

    public String getTollPlaza() {
        return this.tollPlaza;
    }

    public int getLaneNumber() {
        return laneNumber;
    }

    public String getDirectionId() {
        return this.directionId;
    }

    public long getTransactionTimeSeconds() {
        return this.transactionTimeSeconds;
    }

    public long getPrecedingVehicleTransactionTimeSeconds() {
        return precedingVehicleTransactionTimeSeconds;
    }

    public int getRankInQueue() {
        return rankInQueue;
    }

    public double getTimeSpentAtPlazaInSeconds() {
        return this.timeSpentAtPlazaInSeconds;
    }

    public int getDistanceFromDestinationAtStipulatedTime() {
        return distanceFromDestinationAtStipulatedTime;
    }

    public double getAscribedXCoordinateAtStipulatedTime() {
        return this.ascribedXCoordinateAtStipulatedTime;
    }

    public double getAscribedYCoordinateAtStipulatedTime() {
        return this.ascribedYCoordinateAtStipulatedTime;
    }
}