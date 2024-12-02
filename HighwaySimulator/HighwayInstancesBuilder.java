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
            vehicleInstanceWriter.write("VehRegNo,VehClass,AscribedSpeed_ms,PlazaName,LaneNo,TravelDirection," +
                    "TransactionTime_s,PrecVehTransactionTime_s,RankInQueue,TimeAtPlaza_s,DistFromDestAtStipTime_m," +
                    "AscribedXCoordinateStipTime7760,AscribedYCoordinateStipTime7760\n");

            // Write out data
            for (MobileVehicleInstance mobileVehicleInstance : mobileVehicleInstances.values()) {
                vehicleInstanceWriter.write(mobileVehicleInstance.getVehicleRegistration() + "," +
                        mobileVehicleInstance.getEmployeeSpecifiedVehicleClass() + "," +
                        mobileVehicleInstance.getVehicleSpeedMPS() + "," +
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
                    double vehicleSpeedMPS = generateSpeed(employeeSpecifiedVehicleClass);

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

                    long transactionTimeSeconds = vehicleTollingInstance.getTransactionTimeSeconds();
                    // TODO: Set sensitivity of queue-formation using transaction time gaps below at the Boolean gates
                    if ((transactionTimeSeconds - precedingVehicleTransactionTimeSeconds) <= 10) {
                        rankInQueue += 1;
                        timeSpentAtPlazaInSeconds = (vehicleSpeedMPS / AVERAGE_DECELERATION_RATE_MS2) * 2 +
                                TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS +
                                TIME_TAKEN_QUEUED_TOLL_PROCESSING_SECONDS * (rankInQueue - 1);
                    } else {
                        rankInQueue = 1;
                        timeSpentAtPlazaInSeconds = (vehicleSpeedMPS / AVERAGE_DECELERATION_RATE_MS2) * 2 +
                                TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS;
                    }

                    MobileVehicleInstance mobileVehicleInstance = new MobileVehicleInstance(vehicleRegistration,
                            employeeSpecifiedVehicleClass, vehicleSpeedMPS, tollPlazaName, laneNumber, directionId,
                            transactionTimeSeconds, precedingVehicleTransactionTimeSeconds, rankInQueue,
                            timeSpentAtPlazaInSeconds, 0, 0,
                            0);

                    int distanceFromDestinationAtStipulatedTime = assignHighwayDistanceM(mobileVehicleInstance,
                            secondsValueForVisualizingVehicleLocations);
                    mobileVehicleInstance.setDistanceFromDestinationAtStipulatedTime(
                            distanceFromDestinationAtStipulatedTime);

                    if ((distanceFromDestinationAtStipulatedTime >= 0) && (distanceFromDestinationAtStipulatedTime <=
                            DELHI_TO_AGRA_DIST_M)) {
                        if (directionId.equalsIgnoreCase("DA")) {
                            CoordinatesDistances locationForDADirection = distanceVsCoordinatesMapFromDel.get(
                                    distanceFromDestinationAtStipulatedTime);
                            if (locationForDADirection != null) {
                                mobileVehicleInstance.setAscribedXCoordinateAtStipulatedTime(locationForDADirection.
                                        getXCoordinate());
                                mobileVehicleInstance.setAscribedYCoordinateAtStipulatedTime(locationForDADirection.
                                        getYCoordinate());

                                mobileVehicleInstanceMap.put(vehicleRegistration, mobileVehicleInstance);
                            }
                        } else if (directionId.equalsIgnoreCase("AD")) {
                            CoordinatesDistances locationForADDirection = distanceVsCoordinatesMapFromAgr.get(
                                    distanceFromDestinationAtStipulatedTime);
                            if (locationForADDirection != null) {
                                mobileVehicleInstance.setAscribedXCoordinateAtStipulatedTime(locationForADDirection.
                                        getXCoordinate());
                                mobileVehicleInstance.setAscribedYCoordinateAtStipulatedTime(locationForADDirection.
                                        getYCoordinate());

                                mobileVehicleInstanceMap.put(vehicleRegistration, mobileVehicleInstance);
                            }
                        }
                    }

                    /* Debug: Check data records subject to conditionality
                    if ((distanceFromDestinationAtStipulatedTime >= 0) && (distanceFromDestinationAtStipulatedTime <=
                            DELHI_TO_AGRA_DIST_M)) {
                        System.out.println("Mobile vehicle instance details: " + "\n" +
                                "Vehicle registration: " + vehicleRegistration + "\n" +
                                "Employee specified vehicle class: " + employeeSpecifiedVehicleClass + "\n" +
                                "Vehicle speed m/s: " + vehicleSpeedMPS + "\n" +
                                "Toll plaza name: " + tollPlazaName + "\n" +
                                "Lane number: " + laneNumber + "\n" +
                                "Direction of motion: " + directionId + "\n" +
                                "Transaction time (in seconds): " + transactionTimeSeconds + "\n" +
                                "Preceding vehicle transaction time (in seconds): " +
                                precedingVehicleTransactionTimeSeconds + "\n" +
                                "Rank in queue: " + rankInQueue + "\n" +
                                "Time spent at plaza (in seconds): " + timeSpentAtPlazaInSeconds + "\n" +
                                "Distance from destination at stipulated time: " + distanceFromDestinationAtStipulatedTime +
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

        return mobileVehicleInstanceMap;
    }

    private static int assignHighwayDistanceM (MobileVehicleInstance mobileVehicleInstance,
                                       long secondsValueForVisualizingVehicleLocations) {
        String directionId = mobileVehicleInstance.getDirectionId();
        String plazaName = mobileVehicleInstance.getTollPlaza();
        double vehicleSpeedMPS = mobileVehicleInstance.getVehicleSpeedMPS();

        double timeToBackTrackInSeconds = (mobileVehicleInstance.getTransactionTimeSeconds() - mobileVehicleInstance.
                getTimeSpentAtPlazaInSeconds()) - secondsValueForVisualizingVehicleLocations;
        int backtrackingDistanceM = (int) (timeToBackTrackInSeconds * vehicleSpeedMPS);
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

        return distanceAlongHighway;
    }

    private static double generateSpeed(String SVCClass) {
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

        Random random = new Random();
        return random.nextGaussian(averageVehicleClassSpeed, stdDevVehicleClassSpeed);
    }
}