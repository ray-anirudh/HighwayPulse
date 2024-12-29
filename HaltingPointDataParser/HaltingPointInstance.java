package HaltingPointDataParser;

public class HaltingPointInstance {
    private int haltingPointId;
    private String haltingPointDescription;
    private double haltingPointLatitude;
    private double haltingPointLongitude;
    private String haltingPointCategory;
    private String haltingPointSubCategory;
    private String haltingPointDataCollectionTime;
    private double chargerDowntime;
    private double chargerCapacityUtilization;
    private double chargerCount;
    private double totalChargerOutputKW;
    private double aggregateChargingDemandKWh;
    private double averageVehicleChargingProbability;
    private double averageVehicleHaltingDuration;
    private double passengerVehicleCountDesiringCharge;
    private double freightVehicleCountDesiringCharge;
    private double powerOutputRequired;

    public HaltingPointInstance(int haltingPointId, String haltingPointDescription, double haltingPointLatitude,
                                double haltingPointLongitude, String haltingPointCategory, String haltingPointSubCategory,
                                String haltingPointDataCollectionTime, double chargerDowntime,
                                double chargerCapacityUtilization, double chargerCount, double totalChargerOutputKW) {
        this.haltingPointId = haltingPointId;
        this.haltingPointDescription = haltingPointDescription;
        this.haltingPointLatitude = haltingPointLatitude;
        this.haltingPointLongitude = haltingPointLongitude;
        this.haltingPointCategory = haltingPointCategory;
        this.haltingPointSubCategory = haltingPointSubCategory;
        this.haltingPointDataCollectionTime = haltingPointDataCollectionTime;
        this.chargerDowntime = chargerDowntime;
        this.chargerCapacityUtilization = chargerCapacityUtilization;
        this.chargerCount = chargerCount;
        this.totalChargerOutputKW = totalChargerOutputKW;
    }

    public void setHaltingPointId(int haltingPointId) {
        this.haltingPointId = haltingPointId;
    }
    public void setHaltingPointDescription(String haltingPointDescription) {
        this.haltingPointDescription = haltingPointDescription;
    }

    public void setHaltingPointLatitude(double haltingPointLatitude) {
        this.haltingPointLatitude = haltingPointLatitude;
    }

    public void setHaltingPointLongitude(double haltingPointLongitude) {
        this.haltingPointLongitude = haltingPointLongitude;
    }

    public void setHaltingPointCategory(String haltingPointCategory) {
        this.haltingPointCategory = haltingPointCategory;
    }

    public void setHaltingPointSubCategory(String haltingPointSubCategory) {
        this.haltingPointSubCategory = haltingPointSubCategory;
    }

    public void setHaltingPointDataCollectionTime(String haltingPointDataCollectionTime) {
        this.haltingPointDataCollectionTime = haltingPointDataCollectionTime;
    }

    public void setAggregateChargingDemandKWh(double aggregateChargingDemandKWh) {
        this.aggregateChargingDemandKWh = aggregateChargingDemandKWh;
    }

    public void setAverageVehicleChargingProbability(double averageVehicleChargingProbability) {
        this.averageVehicleChargingProbability = averageVehicleChargingProbability;
    }

    public void setAverageVehicleHaltingDuration(double averageVehicleHaltingDuration) {
        this.averageVehicleHaltingDuration = averageVehicleHaltingDuration;
    }

    public void setPassengerVehicleCountDesiringCharge(double passengerVehicleCountDesiringCharge) {
        this.passengerVehicleCountDesiringCharge = passengerVehicleCountDesiringCharge;
    }

    public void setFreightVehicleCountDesiringCharge(double freightVehicleCountDesiringCharge) {
        this.freightVehicleCountDesiringCharge = freightVehicleCountDesiringCharge;
    }

    public void setPowerOutputRequired(double powerOutputRequired) {
        this.powerOutputRequired = powerOutputRequired;
    }

    public int getHaltingPointId() {
        return this.haltingPointId;
    }

    public String getHaltingPointDescription() {
        return this.haltingPointDescription;
    }

    public double getHaltingPointLatitude() {
        return this.haltingPointLatitude;
    }

    public double getHaltingPointLongitude() {
        return this.haltingPointLongitude;
    }

    public String getHaltingPointCategory() {
        return this.haltingPointCategory;
    }

    public String getHaltingPointSubCategory() {
        return this.haltingPointSubCategory;
    }

    public String getHaltingPointDataCollectionTime() {
        return this.haltingPointDataCollectionTime;
    }

    public double getChargerDowntime() {
        return this.chargerDowntime;
    }

    public double getChargerCapacityUtilization() {
        return this.chargerCapacityUtilization;
    }

    public double getChargerCount() {
        return this.chargerCount;
    }

    public double getTotalChargerOutputKW() {
        return this.totalChargerOutputKW;
    }

    public double getAggregateChargingDemandKWh() {
        return this.aggregateChargingDemandKWh;
    }

    public double getAverageVehicleChargingProbability() {
        return this.averageVehicleChargingProbability;
    }

    public double getAverageVehicleHaltingDuration() {
        return this.averageVehicleHaltingDuration;
    }

    public double getPassengerVehicleCountDesiringCharge() {
        return this.passengerVehicleCountDesiringCharge;
    }

    public double getFreightVehicleCountDesiringCharge() {
        return this.freightVehicleCountDesiringCharge;
    }

    public double getPowerOutputRequired() {
        return this.powerOutputRequired;
    }
}