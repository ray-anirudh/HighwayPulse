import Utils.CoordinatesDistances;
import Utils.DateTimeListReader;
import Utils.XYToDistances;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class HighwayInstancesBuilder {
    public static final double AVERAGE_DECELERATION_RATE_MS2 = 2.75;
    public static final int TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS = 3;
    public static final int TIME_TAKEN_QUEUED_TOLL_PROCESSING_SECONDS = 7;
    private static final int OKHLA_TO_GADPURI_DIST_M = 35_750;
    private static final int GADPURI_TO_KARMAN_DIST_M = 48_200;
    private static final int KARMAN_TO_MAHUVAN_DIST_M = 71_100;
    private static final int MAHUVAN_TO_AGRA_BYPASS_DIST_M = 12_165;
    private static final int DELHI_TO_AGRA_DIST_M = OKHLA_TO_GADPURI_DIST_M + GADPURI_TO_KARMAN_DIST_M +
            KARMAN_TO_MAHUVAN_DIST_M + MAHUVAN_TO_AGRA_BYPASS_DIST_M;

    public static void main (String[] args) {
        // Read highway tolling microdata and set up temporal points to visualize highway-wide vehicle locations
        String tollingMicroDataFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "DelAgraTollMicrodataZweiWoche.csv";
        String dateTimeListFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "DateTimeInstancesToCapture.csv";

        TollDataReaderWriter tollDataReaderWriter = new TollDataReaderWriter();
        DateTimeListReader dateTimeListReader = new DateTimeListReader();
        LinkedHashMap<String, LinkedHashMap<Integer, LinkedHashMap<Long, VehicleTollingInstance>>>
                plazaLaneWiseVehicleTollingInstances = tollDataReaderWriter.readTollData(tollingMicroDataFilePath);
        ArrayList<Long> secondsForVisualizingVehicleLocations = dateTimeListReader.readDateTimeList(
                dateTimeListFilePath);

        // Build distance-to-coordinate maps
        XYToDistances xYToDistances = new XYToDistances();
        String XYDataFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "DelAgrRoadStretchAsPoints1m-7760Dissolved.csv";
        xYToDistances.readCoordinatesAndTranslateToDistances(XYDataFilePath);
        LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromDel = xYToDistances.
                getDistanceVsCoordinatesMapFromDel();
        LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromAgr = xYToDistances.
                getDistanceVsCoordinatesMapFromAgr();
        System.exit(1);
        java.awt.Toolkit.getDefaultToolkit().beep();

        String outputFilePathBase = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "MobileVehicleInstances_11.2024/MobileVehicleInstances";
        int counter = 1;

        for (long secondsValueForVisualizingVehicleLocations : secondsForVisualizingVehicleLocations) {
            String mobileVehicleInstancesFilePath = outputFilePathBase + counter + ".csv";
            LinkedHashMap<String, MobileVehicleInstance> mobileVehicleInstances = generateMobileVehicles
                    (plazaLaneWiseVehicleTollingInstances, secondsValueForVisualizingVehicleLocations,
                    distanceVsCoordinatesMapFromDel, distanceVsCoordinatesMapFromAgr);
            writeVehicleInstanceData(mobileVehicleInstancesFilePath, mobileVehicleInstances);
            counter++;
        }
    }

    private static void writeVehicleInstanceData(String vehicleInstanceDataFilePath,
                                                 LinkedHashMap<String, MobileVehicleInstance>
                                                         mobileVehicleInstances) {
        try {
            // Writer for a CSV file containing vehicular coordinates and other information
            BufferedWriter vehicleInstanceWriter = new BufferedWriter(new FileWriter(vehicleInstanceDataFilePath));

            // Write out the header
            vehicleInstanceWriter.write("VehRegNo,VehClass,isEV,AscribedSpeed_ms,ActualSpeed_ms,DidVehicleHalt," +
                    "HaltingDuration_mins,BatteryCapacity_kWh,FuelEconomy_kWhPerKm,StartingSoC_percent," +
                    "CurrentSoC_percent,EnergyRequired_kWh,ChargingProbability,EstimatedArrivalTime_s,PlazaName," +
                    "LaneNo,TravelDirection,TransactionTime_s,PrecVehTransactionTime_s,RankInQueue,TimeAtPlaza_s," +
                    "DistFromDestAtStipTime_m,AscribedXCoordinateStipTime7760,AscribedYCoordinateStipTime7760\n");

            // Write out data
            for (MobileVehicleInstance mobileVehicleInstance : mobileVehicleInstances.values()) {
                vehicleInstanceWriter.write(
                        mobileVehicleInstance.getVehicleRegistration() + "," +
                        mobileVehicleInstance.getEmployeeSpecifiedVehicleClass() + "," +
                        mobileVehicleInstance.getIsEV() + "," +
                        mobileVehicleInstance.getAssignedVehicleSpeedMPS() + "," +
                        mobileVehicleInstance.getActualVehicleSpeedMPS() + "," +
                        mobileVehicleInstance.getDidVehicleHalt() + "," +
                        mobileVehicleInstance.getHaltingDurationMins() + "," +
                        mobileVehicleInstance.getVehicleBatteryCapacityKWh() + "," +
                        mobileVehicleInstance.getVehicleFuelEconomyKWhPKm() + "," +
                        mobileVehicleInstance.getVehicleStartingSoCPercent() + "," +
                        mobileVehicleInstance.getVehicleCurrentSoCLevelPercent() + "," +
                        mobileVehicleInstance.getVehicleEnergyRequired() + "," +
                        mobileVehicleInstance.getVehicleChargingProbability() + "," +
                        mobileVehicleInstance.getEstimatedArrivalTimeSeconds() + "," +
                        mobileVehicleInstance.getTollPlaza() + "," +
                        mobileVehicleInstance.getLaneNumber() + "," +
                        mobileVehicleInstance.getDirectionId() + "," +
                        mobileVehicleInstance.getTransactionTimeSeconds() + "," +
                        mobileVehicleInstance.getPrecedingVehicleTransactionTimeSeconds() + "," +
                        mobileVehicleInstance.getRankInQueue() + "," +
                        mobileVehicleInstance.getTimeSpentAtPlazaInSeconds() + "," +
                        mobileVehicleInstance.getDistanceFromDestinationAtStipulatedTime() + "," +
                        mobileVehicleInstance.getAscribedXCoordinateAtStipulatedTime() + "," +
                        mobileVehicleInstance.getAscribedYCoordinateAtStipulatedTime() + "\n");
            }

            System.out.println("Data of mobile vehicle instances written to " + vehicleInstanceDataFilePath);

        } catch (IOException iOE) {
            System.out.println("Input-output exception; please check the hashmap of mobile vehicle instances.");
        }
    }

    private static LinkedHashMap<String, MobileVehicleInstance> generateMobileVehicles (
            LinkedHashMap<String, LinkedHashMap<Integer, LinkedHashMap<Long, VehicleTollingInstance>>>
                    plazaLaneWiseTollingInstanceMaps, long secondsValueForVisualizingVehicleLocations,
            LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromDel,
            LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromAgr) {

        LinkedHashMap<String, MobileVehicleInstance> mobileVehicleInstanceMap = new LinkedHashMap<>();
        // String keys refer to vehicle registration numbers, and values refer to MobileVehicleInstance objects

        for(String tollPlazaName : plazaLaneWiseTollingInstanceMaps.keySet()) {
            long precedingVehicleTransactionTimeSeconds = 0;
            int rankInQueue = 1;
            double timeSpentAtPlazaInSeconds = 0;
            for (int laneNumber : plazaLaneWiseTollingInstanceMaps.get(tollPlazaName).keySet()) {
                for (HashMap.Entry<Long, VehicleTollingInstance> vehicleTollingInstanceEntry :
                        plazaLaneWiseTollingInstanceMaps.get(tollPlazaName).get(laneNumber).entrySet()) {
                    VehicleTollingInstance vehicleTollingInstance = vehicleTollingInstanceEntry.getValue();

                    String vehicleRegistration = vehicleTollingInstance.getVehicleRegistration();
                    String employeeSpecifiedVehicleClass = vehicleTollingInstance.getEmployeeSpecifiedVehicleClass();
                    double assignedVehicleSpeedMPS = assignSpeed(employeeSpecifiedVehicleClass);

                    Random haltingRandom = new Random();
                    int haltingDecider = haltingRandom.nextInt(5);
                    boolean didVehicleHalt = false;
                    int haltingDurationMins = 0;
                    double actualVehicleSpeedMPS = 0;

                    String directionId = "AD";
                    if (tollPlazaName.equalsIgnoreCase("Gadpuri")) {
                        if ((laneNumber >= 1) && (laneNumber <= 12)) {
                            directionId = "DA";
                        }
                    } else if (tollPlazaName.equalsIgnoreCase("Karman")) {
                        if ((laneNumber >= 1) && (laneNumber <= 8)) {
                            directionId = "DA";
                        }
                    } else if (tollPlazaName.equalsIgnoreCase("Mahuvan")) {
                        if ((laneNumber >= 1) && (laneNumber <= 7)) {
                            directionId = "DA";
                        }
                    }

                    if (haltingDecider % 2 == 0) {
                        didVehicleHalt = true;
                        actualVehicleSpeedMPS = calculateSpeed(employeeSpecifiedVehicleClass);

                        int distanceToUseForHaltingDuration = 0;
                        if (tollPlazaName.equalsIgnoreCase("Gadpuri")) {
                            if (directionId.equalsIgnoreCase("DA")) {
                                distanceToUseForHaltingDuration = OKHLA_TO_GADPURI_DIST_M;
                            } else {
                                distanceToUseForHaltingDuration = GADPURI_TO_KARMAN_DIST_M;
                            }
                        } else if (tollPlazaName.equalsIgnoreCase("Karman")) {
                            if (directionId.equalsIgnoreCase("DA")) {
                                distanceToUseForHaltingDuration = GADPURI_TO_KARMAN_DIST_M;
                            } else {
                                distanceToUseForHaltingDuration = KARMAN_TO_MAHUVAN_DIST_M;
                            }
                        } else if (tollPlazaName.equalsIgnoreCase("Mahuvan")) {
                            if (directionId.equalsIgnoreCase("DA")) {
                                distanceToUseForHaltingDuration = KARMAN_TO_MAHUVAN_DIST_M;
                            } else {
                                distanceToUseForHaltingDuration = MAHUVAN_TO_AGRA_BYPASS_DIST_M;
                            }
                        }

                        haltingDurationMins = (int) (distanceToUseForHaltingDuration /
                                (assignedVehicleSpeedMPS - actualVehicleSpeedMPS));
                    }

                    long transactionTimeSeconds = vehicleTollingInstance.getTransactionTimeSeconds();
                    /**
                     * Set sensitivity of queue-formation using transaction time gaps below at the Boolean gates
                     */
                    if ((transactionTimeSeconds - precedingVehicleTransactionTimeSeconds) <= 10) {
                        rankInQueue += 1;
                        timeSpentAtPlazaInSeconds = (assignedVehicleSpeedMPS / AVERAGE_DECELERATION_RATE_MS2) * 2 +
                                TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS +
                                TIME_TAKEN_QUEUED_TOLL_PROCESSING_SECONDS * (rankInQueue - 1);
                    } else {
                        rankInQueue = 1;
                        timeSpentAtPlazaInSeconds = (assignedVehicleSpeedMPS / AVERAGE_DECELERATION_RATE_MS2) * 2 +
                                TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS;
                    }

                    long estimatedArrivalTimeSeconds = transactionTimeSeconds - (long) timeSpentAtPlazaInSeconds;
                    boolean isEV = makeEVOrNot(employeeSpecifiedVehicleClass);
                    double vehicleBatteryCapacityKWh = 0;
                    double vehicleFuelEconomyKWhPKm = 0;
                    double vehicleStartingSoCPercent = 0;
                    double vehicleCurrentSoCLevelPercent = 0;
                    double vehicleEnergyRequired = 0;
                    double vehicleChargingProbability = 0;

                    if (isEV) {
                        vehicleBatteryCapacityKWh = assignBatteryCapacity(employeeSpecifiedVehicleClass);
                        vehicleFuelEconomyKWhPKm = assignFuelEconomy(employeeSpecifiedVehicleClass);
                        vehicleStartingSoCPercent = assignStartingSoCLevel(employeeSpecifiedVehicleClass, tollPlazaName,
                                directionId);
                    }


                    MobileVehicleInstance mobileVehicleInstance = new MobileVehicleInstance(vehicleRegistration,
                            employeeSpecifiedVehicleClass, isEV, assignedVehicleSpeedMPS, actualVehicleSpeedMPS,
                            didVehicleHalt, haltingDurationMins, vehicleBatteryCapacityKWh, vehicleFuelEconomyKWhPKm,
                            vehicleStartingSoCPercent, vehicleCurrentSoCLevelPercent, vehicleEnergyRequired,
                            vehicleChargingProbability, tollPlazaName, laneNumber, directionId, transactionTimeSeconds,
                            estimatedArrivalTimeSeconds, precedingVehicleTransactionTimeSeconds,
                            rankInQueue, timeSpentAtPlazaInSeconds, 0,
                            0, 0);

                    int distanceFromOriginAtStipulatedTime = assignHighwayDistanceM(mobileVehicleInstance,
                            secondsValueForVisualizingVehicleLocations);
                    mobileVehicleInstance.setDistanceFromDestinationAtStipulatedTime(
                            distanceFromOriginAtStipulatedTime);

                    if (isEV) {
                        vehicleCurrentSoCLevelPercent = assignCurrentSoCLevel(vehicleStartingSoCPercent,
                                vehicleFuelEconomyKWhPKm, vehicleBatteryCapacityKWh,
                                distanceFromOriginAtStipulatedTime);
                        vehicleEnergyRequired = getVehicleEnergyRequired(vehicleCurrentSoCLevelPercent,
                                vehicleBatteryCapacityKWh);
                        vehicleChargingProbability = assignVehicleChargingProbability(vehicleCurrentSoCLevelPercent);
//                        System.out.println(vehicleChargingProbability);

                        mobileVehicleInstance.setVehicleCurrentSoCLevelPercent(vehicleCurrentSoCLevelPercent);
                        mobileVehicleInstance.setVehicleEnergyRequired(vehicleEnergyRequired);
                        mobileVehicleInstance.setVehicleEnergyRequired(vehicleChargingProbability);
                    }

                    if ((distanceFromOriginAtStipulatedTime >= 0) && (distanceFromOriginAtStipulatedTime <=
                            DELHI_TO_AGRA_DIST_M)) {
                        if (directionId.equalsIgnoreCase("DA")) {
                            CoordinatesDistances locationForDADirection = distanceVsCoordinatesMapFromDel.get(
                                    distanceFromOriginAtStipulatedTime);
                            if (locationForDADirection != null) {
                                mobileVehicleInstance.setAscribedXCoordinateAtStipulatedTime(locationForDADirection.
                                        getXCoordinate());
//                                System.out.println("X" + locationForDADirection.getXCoordinate());
//                                System.out.println("Y" + locationForDADirection.getYCoordinate());
                                mobileVehicleInstance.setAscribedYCoordinateAtStipulatedTime(locationForDADirection.
                                        getYCoordinate());

                                mobileVehicleInstanceMap.put(vehicleRegistration, mobileVehicleInstance);
                            }
                        } else if (directionId.equalsIgnoreCase("AD")) {
                            CoordinatesDistances locationForADDirection = distanceVsCoordinatesMapFromAgr.get(
                                    distanceFromOriginAtStipulatedTime);
                            if (locationForADDirection != null) {
                                mobileVehicleInstance.setAscribedXCoordinateAtStipulatedTime(locationForADDirection.
                                        getXCoordinate());
                                mobileVehicleInstance.setAscribedYCoordinateAtStipulatedTime(locationForADDirection.
                                        getYCoordinate());
//                                System.out.println("X" + locationForADDirection.getXCoordinate());
//                                System.out.println("Y" + locationForADDirection.getYCoordinate());
//                                System.out.println(mobileVehicleInstance.getVehicleChargingProbability());

                                mobileVehicleInstanceMap.put(vehicleRegistration, mobileVehicleInstance);
                            }
                        }
                    }

                    /* Debug: Check data records subject to conditionality
                    if ((distanceFromoriginAtStipulatedTime >= 0) && (distanceFromoriginAtStipulatedTime <=
                            DELHI_TO_AGRA_DIST_M)) {
                        System.out.println("Mobile vehicle instance details: " + "\n" +
                                "Vehicle registration: " + vehicleRegistration + "\n" +
                                "Employee specified vehicle class: " + employeeSpecifiedVehicleClass + "\n" +
                                "Vehicle speed m/s: " + assignedVehicleSpeedMPS + "\n" +
                                "Toll plaza name: " + tollPlazaName + "\n" +
                                "Lane number: " + laneNumber + "\n" +
                                "Direction of motion: " + directionId + "\n" +
                                "Transaction time (in seconds): " + transactionTimeSeconds + "\n" +
                                "Preceding vehicle transaction time (in seconds): " +
                                precedingVehicleTransactionTimeSeconds + "\n" +
                                "Rank in queue: " + rankInQueue + "\n" +
                                "Time spent at plaza (in seconds): " + timeSpentAtPlazaInSeconds + "\n" +
                                "Distance from destination at stipulated time: " + distanceFromoriginAtStipulatedTime +
                                "\n" +
                                "Ascribed x-coordinate at stipulated time: " + mobileVehicleInstance.
                                getAscribedXCoordinateAtStipulatedTime() + "\n" +
                                "Ascribed y-coordinate at stipulated time: " + mobileVehicleInstance.
                                getAscribedYCoordinateAtStipulatedTime() + "\n");
                    }
                    */

                    precedingVehicleTransactionTimeSeconds = transactionTimeSeconds;
                }
            }
        }

        // todo ensure variable value stability across timestamps or generate only for one time stamp

        return mobileVehicleInstanceMap;
    }

    private static boolean makeEVOrNot(String SVCClass) {
        boolean isEV = false;
        Random eVMakerRandom = new Random();
        double eVDeciderValue = eVMakerRandom.nextFloat(1);

        double eVPenetrationRateCarJeep = 0.4;
        double eVPenetrationRateLCV = 0.2;
        double eVPenetrationRateTruck = 0.1;
        double eVPenetrationRateBus = 0.3;
        double eVPenetrationRateMAV = 0.1;
        double eVPenetrationRateTractor = 0.05;

        switch (SVCClass) {
            case "CarJeep" -> {if (eVDeciderValue > eVPenetrationRateCarJeep) {isEV = true;}}
            case "LCV" -> {if (eVDeciderValue > eVPenetrationRateLCV) {isEV = true;}}
            case "Truck" -> {if (eVDeciderValue > eVPenetrationRateTruck) {isEV = true;}}
            case "Bus" -> {if (eVDeciderValue > eVPenetrationRateBus) {isEV = true;}}
            case "MAV" -> {if (eVDeciderValue > eVPenetrationRateMAV) {isEV = true;}}
            case "Tractor" -> {if (eVDeciderValue > eVPenetrationRateTractor) {isEV = true;}}
        }

        return isEV;
    }

    private static int assignHighwayDistanceM (MobileVehicleInstance mobileVehicleInstance,
                                       long secondsValueForVisualizingVehicleLocations) {
        String directionId = mobileVehicleInstance.getDirectionId();
        String plazaName = mobileVehicleInstance.getTollPlaza();
        double vehicleSpeedMPS = mobileVehicleInstance.getAssignedVehicleSpeedMPS();

        double timeToBackTrackInSeconds = (mobileVehicleInstance.getTransactionTimeSeconds() - mobileVehicleInstance.
                getTimeSpentAtPlazaInSeconds()) - secondsValueForVisualizingVehicleLocations;
//        System.out.println("LOL");
//        System.out.println(mobileVehicleInstance.getTransactionTimeSeconds());
//        System.out.println(mobileVehicleInstance.getTimeSpentAtPlazaInSeconds());
//        System.out.println(secondsValueForVisualizingVehicleLocations);
        int backtrackingDistanceM = (int) (timeToBackTrackInSeconds * vehicleSpeedMPS);

//        System.out.println("Vehicle speed " + vehicleSpeedMPS);
//        System.out.println("Time to backtrack " + timeToBackTrackInSeconds);
//        System.out.println(backtrackingDistanceM);
        int distanceAlongHighway = -1;

        if (directionId.equalsIgnoreCase("DA")) {
            if (plazaName.equalsIgnoreCase("Mahuvan")) {
                distanceAlongHighway = DELHI_TO_AGRA_DIST_M - MAHUVAN_TO_AGRA_BYPASS_DIST_M - backtrackingDistanceM;
            } else if (plazaName.equalsIgnoreCase("Karman")) {
                distanceAlongHighway = DELHI_TO_AGRA_DIST_M - MAHUVAN_TO_AGRA_BYPASS_DIST_M - KARMAN_TO_MAHUVAN_DIST_M -
                        backtrackingDistanceM;
            } else if (plazaName.equalsIgnoreCase("Gadpuri")) {
                distanceAlongHighway = DELHI_TO_AGRA_DIST_M - MAHUVAN_TO_AGRA_BYPASS_DIST_M - KARMAN_TO_MAHUVAN_DIST_M -
                        GADPURI_TO_KARMAN_DIST_M - backtrackingDistanceM;
            }
        } else if (directionId.equalsIgnoreCase("AD")) {
            if (plazaName.equalsIgnoreCase("Gadpuri")) {
                distanceAlongHighway = DELHI_TO_AGRA_DIST_M - OKHLA_TO_GADPURI_DIST_M - backtrackingDistanceM;
            } else if (plazaName.equalsIgnoreCase("Karman")) {
                distanceAlongHighway = DELHI_TO_AGRA_DIST_M - OKHLA_TO_GADPURI_DIST_M - GADPURI_TO_KARMAN_DIST_M -
                        backtrackingDistanceM;
            } else if (plazaName.equalsIgnoreCase("Mahuvan")) {
                distanceAlongHighway = DELHI_TO_AGRA_DIST_M - OKHLA_TO_GADPURI_DIST_M - GADPURI_TO_KARMAN_DIST_M -
                        KARMAN_TO_MAHUVAN_DIST_M - backtrackingDistanceM;
            }
        }

//        System.out.println("Distance along highway: " + distanceAlongHighway);
        return distanceAlongHighway;
    }

    private static double calculateSpeed(String SVCClass) {
        double averageVehicleClassSpeed = 15.44;
        double stdDevVehicleClassSpeed = 1.77;

        switch (SVCClass) {
                case "CarJeep" -> {averageVehicleClassSpeed = 16.37; stdDevVehicleClassSpeed = 2.08;}
                case "LCV" -> {averageVehicleClassSpeed = 23.62; stdDevVehicleClassSpeed = 2.72;}
                case "Truck" -> {averageVehicleClassSpeed = 14.18; stdDevVehicleClassSpeed = 1.88;}
                case "Bus" -> {averageVehicleClassSpeed = 10.04; stdDevVehicleClassSpeed = 1.24;}
                case "MAV" -> {averageVehicleClassSpeed = 9.07; stdDevVehicleClassSpeed = 0.96;}
                case "Tractor" -> {averageVehicleClassSpeed = 10.58; stdDevVehicleClassSpeed = 0.72;}
        }

        Random actualSpeedRandom = new Random();
        return actualSpeedRandom.nextGaussian(averageVehicleClassSpeed, stdDevVehicleClassSpeed);
    }

    private static double assignBatteryCapacity(String SVCClass) {
        double averageVehicleBatteryCapacity = 25;
        double stdDevVehicleBatteryCapacity = 4;
        switch (SVCClass) {
            case "CarJeep" -> {averageVehicleBatteryCapacity = 28; stdDevVehicleBatteryCapacity = 3;}
            case "LCV" -> {averageVehicleBatteryCapacity = 24; stdDevVehicleBatteryCapacity = 2;}
            case "Truck" -> {averageVehicleBatteryCapacity = 78; stdDevVehicleBatteryCapacity = 4;}
            case "Bus" -> {averageVehicleBatteryCapacity = 250; stdDevVehicleBatteryCapacity = 8;}
            case "MAV" -> {averageVehicleBatteryCapacity = 400; stdDevVehicleBatteryCapacity = 15;}
            case "Tractor" -> {averageVehicleBatteryCapacity = 50; stdDevVehicleBatteryCapacity = 3;}
        }

        Random assignedBatteryCapacityRandom = new Random();
        return assignedBatteryCapacityRandom.nextGaussian(averageVehicleBatteryCapacity, stdDevVehicleBatteryCapacity);
    }

    private static double assignFuelEconomy(String SVCClass) {
        double averageVehicleFuelEconomy = 0.15;
        double stdDevVehicleFuelEconomy = 0.02;
        switch (SVCClass) {
            case "CarJeep" -> {averageVehicleFuelEconomy = 0.18; stdDevVehicleFuelEconomy = 0.04;}
            case "LCV" -> {averageVehicleFuelEconomy = 0.28; stdDevVehicleFuelEconomy = 0.03;}
            case "Truck" -> {averageVehicleFuelEconomy = 0.80; stdDevVehicleFuelEconomy = 0.10;}
            case "Bus" -> {averageVehicleFuelEconomy = 1.30; stdDevVehicleFuelEconomy = 0.40;}
            case "MAV" -> {averageVehicleFuelEconomy = 1.75; stdDevVehicleFuelEconomy = 0.25;}
            case "Tractor" -> {averageVehicleFuelEconomy = 1.2; stdDevVehicleFuelEconomy = 0.14;}
        }

        Random assignedFuelEconomyRandom = new Random();
        return assignedFuelEconomyRandom.nextGaussian(averageVehicleFuelEconomy, stdDevVehicleFuelEconomy);
    }

    private static double assignStartingSoCLevel(String SVCClass, String tollPlazaName, String directionId) {
        double averageStartingSoCLevel = 50;
        double stdDevStartingSoCLevel = 10;

        if (tollPlazaName.equalsIgnoreCase("Gadpuri")) {
            if (directionId.equalsIgnoreCase("DA")) {
                switch (SVCClass) {
                    case "CarJeep" -> {averageStartingSoCLevel = 70; stdDevStartingSoCLevel = 7;}
                    case "LCV" -> {averageStartingSoCLevel = 50; stdDevStartingSoCLevel = 6;}
                    case "Truck" -> {averageStartingSoCLevel = 57; stdDevStartingSoCLevel = 10;}
                    case "Bus" -> {averageStartingSoCLevel = 90; stdDevStartingSoCLevel = 2;}
                    case "MAV" -> {averageStartingSoCLevel = 57; stdDevStartingSoCLevel = 10;}
                    case "Tractor" -> {averageStartingSoCLevel = 50; stdDevStartingSoCLevel = 6;}
                }
            } else {
                switch (SVCClass) {
                    case "CarJeep" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 7;}
                    case "LCV" -> {averageStartingSoCLevel = 28; stdDevStartingSoCLevel = 5;}
                    case "Truck" -> {averageStartingSoCLevel = 37; stdDevStartingSoCLevel = 10;}
                    case "Bus" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 2;}
                    case "MAV" -> {averageStartingSoCLevel = 37; stdDevStartingSoCLevel = 10;}
                    case "Tractor" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 6;}
                }
            }
        } else if (tollPlazaName.equalsIgnoreCase("Karman")) {
            switch (SVCClass) {
                case "CarJeep" -> {averageStartingSoCLevel = 50; stdDevStartingSoCLevel = 7;}
                case "LCV" -> {averageStartingSoCLevel = 39; stdDevStartingSoCLevel = 6;}
                case "Truck" -> {averageStartingSoCLevel = 47; stdDevStartingSoCLevel = 10;}
                case "Bus" -> {averageStartingSoCLevel = 60; stdDevStartingSoCLevel = 6;}
                case "MAV" -> {averageStartingSoCLevel = 47; stdDevStartingSoCLevel = 8;}
                case "Tractor" -> {averageStartingSoCLevel = 40; stdDevStartingSoCLevel = 6;}
            }
        } else if (tollPlazaName.equalsIgnoreCase("Mahuvan")) {
            if (directionId.equalsIgnoreCase("DA")) {
                switch (SVCClass) {
                    case "CarJeep" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 7;}
                    case "LCV" -> {averageStartingSoCLevel = 28; stdDevStartingSoCLevel = 5;}
                    case "Truck" -> {averageStartingSoCLevel = 37; stdDevStartingSoCLevel = 10;}
                    case "Bus" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 2;}
                    case "MAV" -> {averageStartingSoCLevel = 37; stdDevStartingSoCLevel = 10;}
                    case "Tractor" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 6;}
                }
            } else {
                switch (SVCClass) {
                    case "CarJeep" -> {averageStartingSoCLevel = 70; stdDevStartingSoCLevel = 7;}
                    case "LCV" -> {averageStartingSoCLevel = 50; stdDevStartingSoCLevel = 6;}
                    case "Truck" -> {averageStartingSoCLevel = 57; stdDevStartingSoCLevel = 10;}
                    case "Bus" -> {averageStartingSoCLevel = 90; stdDevStartingSoCLevel = 2;}
                    case "MAV" -> {averageStartingSoCLevel = 57; stdDevStartingSoCLevel = 10;}
                    case "Tractor" -> {averageStartingSoCLevel = 50; stdDevStartingSoCLevel = 6;}
                }
            }
        }

        Random startingSoCLevelRandom = new Random();
        return startingSoCLevelRandom.nextGaussian(averageStartingSoCLevel, stdDevStartingSoCLevel);
    }

    private static double assignCurrentSoCLevel(double startingSoCLevel, double fuelEconomy, double batteryCapacity,
                                             int distanceTravelledM) {
//        System.out.println(distanceTravelledM);
        int criticalSoCLevel = 15;
        int maxSoCLevel = 100;
        int mPerKm = 1_000;

        double distanceTravelledPerVehicleCycleM = (((double) (maxSoCLevel - criticalSoCLevel)) / 100) *
                batteryCapacity / fuelEconomy * mPerKm;
        System.out.println("Travelled distance: " + distanceTravelledM);
        System.out.println("Per cycle distance: " + distanceTravelledPerVehicleCycleM);
        int numberOfSelfManagedCycles = (int) (distanceTravelledM / distanceTravelledPerVehicleCycleM);
//        System.out.println(numberOfSelfManagedCycles);

        double currentSoCLevel = startingSoCLevel - (fuelEconomy * (distanceTravelledM - numberOfSelfManagedCycles *
                distanceTravelledPerVehicleCycleM) / batteryCapacity);
        System.out.println("Starting SoC Level: " + startingSoCLevel);
//        System.out.println(currentSoCLevel);

        return (currentSoCLevel < criticalSoCLevel ? criticalSoCLevel : currentSoCLevel);
    }

    private static double getVehicleEnergyRequired(double currentSoCLevel, double batteryCapacity) {
        int criticalSoCLevel = 15;
        return batteryCapacity * (currentSoCLevel - criticalSoCLevel);
    }

    private static double assignVehicleChargingProbability(double currentSoCLevel) {
//        System.out.println(currentSoCLevel);
        int criticalSoCLevel = 15;
        int maxSoCLevel = 100;

        // TODO: Current SoC levels should change given the vehicle types (has to be exogenous)

//        System.out.println("Prob = " + (1 - (currentSoCLevel / (maxSoCLevel - criticalSoCLevel))));
        return (1 - (currentSoCLevel / (maxSoCLevel - criticalSoCLevel)));
    }

    private static double assignSpeed(String SVCClass) {
        double averageVehicleClassSpeed = 19.44;
        double stdDevVehicleClassSpeed = 2.77;
        switch (SVCClass) {
            case "CarJeep" -> {averageVehicleClassSpeed = 22.77; stdDevVehicleClassSpeed = 3.05;}
            case "LCV" -> {averageVehicleClassSpeed = 19.72; stdDevVehicleClassSpeed = 3.88;}
            case "Truck" -> {averageVehicleClassSpeed = 14.44; stdDevVehicleClassSpeed = 3.88;}
            case "Bus" -> {averageVehicleClassSpeed = 17.50; stdDevVehicleClassSpeed = 1.94;}
            case "MAV" -> {averageVehicleClassSpeed = 12.77; stdDevVehicleClassSpeed = 1.66;}
            case "Tractor" -> {averageVehicleClassSpeed = 8.33; stdDevVehicleClassSpeed = 1.94;}
        }

        Random assignedSpeedRandom = new Random();
        return assignedSpeedRandom.nextGaussian(averageVehicleClassSpeed, stdDevVehicleClassSpeed);
    }
}