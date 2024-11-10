public class VehicleTollingInstance {
    /**
     * Attribute definitions
     */

    // Vehicle details
    private String vehicleRegistration;
    private String vehicleFASTagId;
    private int vehicleWeightInMotion;
    private int vehicleWeightStatic;
    private int permissibleVehicleWeightStatic;
    private String autoIdentifiedVehicleClass;
    private String employeeSpecifiedVehicleClass;

    // Transaction details
    private long transactionId;
    private String bankTransactionId;
    private long transactionTimeSeconds;
    private int roadUsageFare;
    private int overloadingFare;
    private int penaltyFare;
    private int totalFare;
    private String paymentMode;
    private String transactionRemark;
    private String journeyType;

    // Infrastructure details
    private String employeeId;
    private int laneNumber;
    private String shiftOrder;
    private String locationName;

    /**
     * Behaviour definitions
     */

    VehicleTollingInstance(String vehicleRegistration, String vehicleFASTagId, int vehicleWeightInMotion,
                           int vehicleWeightStatic, int permissibleVehicleWeightStatic,
                           String autoIdentifiedVehicleClass, String employeeSpecifiedVehicleClass,

                           long transactionId, String bankTransactionId, long transactionTimeSeconds, int roadUsageFare,
                           int overloadingFare, int penaltyFare, int totalFare, String paymentMode,
                           String transactionRemark, String journeyType,

                           String employeeId, int laneNumber, String shiftOrder, String locationName) {
        this.vehicleRegistration = vehicleRegistration;
        this.vehicleFASTagId = vehicleFASTagId;
        this.vehicleWeightInMotion = vehicleWeightInMotion;
        this.vehicleWeightStatic = vehicleWeightStatic;
        this.permissibleVehicleWeightStatic = permissibleVehicleWeightStatic;
        this.autoIdentifiedVehicleClass = autoIdentifiedVehicleClass;
        this.employeeSpecifiedVehicleClass = employeeSpecifiedVehicleClass;

        this.transactionId = transactionId;
        this.bankTransactionId = bankTransactionId;
        this.transactionTimeSeconds = transactionTimeSeconds;
        this.roadUsageFare = roadUsageFare;
        this.overloadingFare = overloadingFare;
        this.penaltyFare = penaltyFare;
        this.totalFare = totalFare;
        this.paymentMode = paymentMode;
        this.transactionRemark = transactionRemark;
        this.journeyType = journeyType;

        this.employeeId = employeeId;
        this.laneNumber = laneNumber;
        this.shiftOrder = shiftOrder;
        this.locationName = locationName;
    }

    public String getVehicleRegistration() {
        return this.vehicleRegistration;
    }

    public String getVehicleFASTagId() {
        return this.vehicleFASTagId;
    }

    public int getVehicleWeightInMotion() {
        return this.vehicleWeightInMotion;
    }

    public int getVehicleWeightStatic() {
        return this.vehicleWeightStatic;
    }

    public int getPermissibleVehicleWeightStatic() {
        return this.permissibleVehicleWeightStatic;
    }

    public String getAutoIdentifiedVehicleClass() {
        return this.autoIdentifiedVehicleClass;
    }

    public String getEmployeeSpecifiedVehicleClass() {
        return this.employeeSpecifiedVehicleClass;
    }

    public long getTransactionId() {
        return this.transactionId;
    }

    public String getBankTransactionId() {
        return this.bankTransactionId;
    }

    public long getTransactionTimeSeconds() {
        return this.transactionTimeSeconds;
    }

    public int getRoadUsageFare() {
        return this.roadUsageFare;
    }

    public int getOverloadingFare() {
        return this.overloadingFare;
    }

    public int getPenaltyFare() {
        return this.penaltyFare;
    }

    public int getTotalFare() {
        return this.totalFare;
    }

    public String getPaymentMode() {
        return this.paymentMode;
    }

    public String getTransactionRemark() {
        return this.transactionRemark;
    }

    public String getJourneyType() {
        return this.journeyType;
    }

    public String getEmployeeId() {
        return this.employeeId;
    }

    public int getLaneNumber() {
        return this.laneNumber;
    }

    public String getShiftOrder() {
        return this.shiftOrder;
    }

    public String getLocationName() {
        return this.locationName;
    }
}