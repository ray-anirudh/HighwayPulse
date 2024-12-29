import HaltingPointDataParser.HaltingPointInstance;

public class MobileVehicleInstance {
    private String vehicleRegistration;
    private String employeeSpecifiedVehicleClass;
    private boolean isEV;
    private double assignedVehicleSpeedMPS;
    private double actualVehicleSpeedMPS;   // Distribution must explicitly trail the one for assigned speeds
    private boolean didVehicleHalt;
    private double haltingDurationSecs;
    private double vehicleBatteryCapacityKWh;
    private double vehicleFuelEconomyKWhPKm;
    private double vehicleStartingSoCPercent;
    private double vehicleCurrentSoCLevelPercent;
    private double vehicleEnergyRequired;
    private double vehicleChargingProbability;
    private String tollPlaza;
    private int laneNumber;
    private String directionId;
    // Can be DA (for Delhi-Agra direction) or AD (for Agra-Delhi direction); proxy for destination location
    private long transactionTimeSeconds;
    private long estimatedArrivalTimeSeconds;
    // Tolling event deceleration-discounted arrival time of a vehicle at a plaza
    private long precedingVehicleTransactionTimeSeconds;
    // Preceding vehicle is one that shares the same plaza name and lane number combination
    private int rankInQueue;
    // Also flags whether queuing occurs
    private double timeSpentAtPlazaInSeconds;
    private int distanceFromOriginAtStipulatedTime;
    private double ascribedXCoordinateAtStipulatedTime;
    private double ascribedYCoordinateAtStipulatedTime;
    private HaltingPointInstance nearestHaltingPointInstance;

    MobileVehicleInstance(String vehicleRegistration, String employeeSpecifiedVehicleClass, boolean isEV,
                          double assignedVehicleSpeedMPS, double actualVehicleSpeedMPS, boolean didVehicleHalt,
                          double haltingDurationSecs, double vehicleBatteryCapacityKWh, double vehicleFuelEconomyKWhPKm,
                          double vehicleStartingSoCPercent, double vehicleCurrentSoCLevelPercent,
                          double vehicleEnergyRequired, double vehicleChargingProbability, String tollPlaza,
                          int laneNumber, String directionId, long transactionTimeSeconds,
                          long estimatedArrivalTimeSeconds, long precedingVehicleTransactionTimeSeconds,
                          int rankInQueue, double timeSpentAtPlazaInSeconds,
                          int distanceFromOriginAtStipulatedTime, double ascribedXCoordinateAtStipulatedTime,
                          double ascribedYCoordinateAtStipulatedTime) {
        this.vehicleRegistration = vehicleRegistration;
        this.employeeSpecifiedVehicleClass = employeeSpecifiedVehicleClass;
        this.isEV = isEV;
        this.assignedVehicleSpeedMPS = assignedVehicleSpeedMPS;
        this.actualVehicleSpeedMPS = actualVehicleSpeedMPS;
        this.didVehicleHalt = didVehicleHalt;
        this.haltingDurationSecs = haltingDurationSecs;
        this.vehicleBatteryCapacityKWh = vehicleBatteryCapacityKWh;
        this.vehicleFuelEconomyKWhPKm = vehicleFuelEconomyKWhPKm;
        this.vehicleStartingSoCPercent = vehicleStartingSoCPercent;
        this.vehicleCurrentSoCLevelPercent = vehicleCurrentSoCLevelPercent;
        this.vehicleEnergyRequired = vehicleEnergyRequired;
        this.vehicleChargingProbability = vehicleChargingProbability;
        this.estimatedArrivalTimeSeconds = estimatedArrivalTimeSeconds;
        this.tollPlaza = tollPlaza;
        this.laneNumber = laneNumber;
        this.directionId = directionId;
        this.transactionTimeSeconds = transactionTimeSeconds;
        this.precedingVehicleTransactionTimeSeconds = precedingVehicleTransactionTimeSeconds;
        this.rankInQueue = rankInQueue;
        this.timeSpentAtPlazaInSeconds = timeSpentAtPlazaInSeconds;
        this.distanceFromOriginAtStipulatedTime = distanceFromOriginAtStipulatedTime;
        this.ascribedXCoordinateAtStipulatedTime = ascribedXCoordinateAtStipulatedTime;
        this.ascribedYCoordinateAtStipulatedTime = ascribedYCoordinateAtStipulatedTime;
    }

    public void setDistanceFromOriginAtStipulatedTime(int distanceFromOriginAtStipulatedTime) {
        this.distanceFromOriginAtStipulatedTime = distanceFromOriginAtStipulatedTime;
    }

    public void setAscribedXCoordinateAtStipulatedTime(double xCoordinateAtStipulatedTime) {
        this.ascribedXCoordinateAtStipulatedTime = xCoordinateAtStipulatedTime;
    }

    public void setAscribedYCoordinateAtStipulatedTime(double yCoordinateAtStipulatedTime) {
        this.ascribedYCoordinateAtStipulatedTime = yCoordinateAtStipulatedTime;
    }

    public void setVehicleCurrentSoCLevelPercent(double vehicleCurrentSoCLevelPercent) {
        this.vehicleCurrentSoCLevelPercent = vehicleCurrentSoCLevelPercent;
    }

    public void setVehicleEnergyRequired(double vehicleEnergyRequired) {
        this.vehicleEnergyRequired = vehicleEnergyRequired;
    }

    public void setVehicleChargingProbability(double vehicleChargingProbability) {
        this.vehicleChargingProbability = vehicleChargingProbability;
    }

    public void setNearestHaltingPointInstance(HaltingPointInstance nearestHaltingPointInstance) {
        this.nearestHaltingPointInstance = nearestHaltingPointInstance;
    }

    public String getVehicleRegistration() {
        return vehicleRegistration;
    }

    public String getEmployeeSpecifiedVehicleClass() {
        return employeeSpecifiedVehicleClass;
    }

    public boolean getIsEV() {
        return this.isEV;
    }

    public double getAssignedVehicleSpeedMPS() {
        return this.assignedVehicleSpeedMPS;
    }

    public double getActualVehicleSpeedMPS() {
        return this.actualVehicleSpeedMPS;
    }

    public boolean getDidVehicleHalt() {
        return this.didVehicleHalt;
    }

    public double getHaltingDurationSecs() {
        return this.haltingDurationSecs;
    }

    public double getVehicleBatteryCapacityKWh() {
        return this.vehicleBatteryCapacityKWh;
    }

    public double getVehicleFuelEconomyKWhPKm() {
        return this.vehicleFuelEconomyKWhPKm;
    }

    public double getVehicleStartingSoCPercent() {
        return this.vehicleStartingSoCPercent;
    }

    public double getVehicleCurrentSoCLevelPercent() {
        return this.vehicleCurrentSoCLevelPercent;
    }

    public double getVehicleEnergyRequired() {
        return this.vehicleEnergyRequired;
    }

    public double getVehicleChargingProbability() {
        return this.vehicleChargingProbability;
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

    public long getEstimatedArrivalTimeSeconds() {
        return this.estimatedArrivalTimeSeconds;
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

    public int getDistanceFromOriginAtStipulatedTime() {
        return distanceFromOriginAtStipulatedTime;
    }

    public double getAscribedXCoordinateAtStipulatedTime() {
        return this.ascribedXCoordinateAtStipulatedTime;
    }

    public double getAscribedYCoordinateAtStipulatedTime() {
        return this.ascribedYCoordinateAtStipulatedTime;
    }

    public HaltingPointInstance getNearestHaltingPointInstance() {
        return this.nearestHaltingPointInstance;
    }
}