import HaltingPointDataParser.HaltingPointInstance;
import HaltingPointDataParser.HaltingPointReaderWriter;
import Utils.CoordinatesDistances;
import Utils.DateTimeListReader;
import Utils.XYToDistances;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

// todo use the csvs to write code descriptions as blocks, or just use ChatGPT

public class HighwayInstancesBuilder {
    public static final double AVERAGE_DECELERATION_RATE_MS2 = 2.75;
    public static final int TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS = 7;
    public static final int TIME_TAKEN_QUEUED_TOLL_PROCESSING_SECONDS = 12;
    private static final int OKHLA_TO_GADPURI_DIST_M = 35_750;
    private static final int GADPURI_TO_KARMAN_DIST_M = 48_200;
    private static final int KARMAN_TO_MAHUVAN_DIST_M = 71_100;
    private static final int MAHUVAN_TO_AGRA_BYPASS_DIST_M = 12_165;
    private static final int DELHI_TO_AGRA_DIST_M = OKHLA_TO_GADPURI_DIST_M + GADPURI_TO_KARMAN_DIST_M +
            KARMAN_TO_MAHUVAN_DIST_M + MAHUVAN_TO_AGRA_BYPASS_DIST_M;

    public static void main(String[] args) {
        // Read highway tolling microdata and set up temporal points to visualize highway-wide vehicle instances
        String tollingMicroDataFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "DelAgraTollMicrodataZweiWoche.csv";
        String dateTimeListFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "DateTimeInstancesToCapture.csv";
        TollDataReaderWriter tollDataReaderWriter = new TollDataReaderWriter();
        DateTimeListReader dateTimeListReader = new DateTimeListReader();
        LinkedHashMap<String, LinkedHashMap<Integer, LinkedHashMap<Long, VehicleTollingInstance>>>
                plazaLaneWiseVehicleTollingInstances = tollDataReaderWriter.readTollData(tollingMicroDataFilePath);
        ArrayList<Long> secondsForBuildingInstances = dateTimeListReader.readDateTimeList(dateTimeListFilePath);
        // Time noting is consistent between date-time and microdata readers

        // Build distance-to-coordinate maps
        XYToDistances xYToDistances = new XYToDistances();
        String XYDataFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "DelAgrRoadStretchAsPoints1m-7760Dissolved.csv";
        xYToDistances.readCoordinatesAndTranslateToDistances(XYDataFilePath);
        LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromDel = xYToDistances.
                getDistanceVsCoordinatesMapFromDel();
        LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromAgr = xYToDistances.
                getDistanceVsCoordinatesMapFromAgr();

        // Read all points of interest
        HaltingPointReaderWriter haltingPointReaderWriter = new HaltingPointReaderWriter();
        String poIDataFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "PointsOfInterestNH44.csv";
        haltingPointReaderWriter.readPoIData(poIDataFilePath);
        LinkedHashMap<Integer, HaltingPointInstance> haltingPointsMap = haltingPointReaderWriter.getHaltingPoints();
        List<HaltingPointInstance> haltingPoints = haltingPointsMap.values().stream().toList();

        // Build master lists for checking vehicles and halting points across different time stamps
        ArrayList<LinkedHashMap<String, MobileVehicleInstance>> mobileVehicleInstancesByTimeStamps = new ArrayList<>();
        ArrayList<LinkedHashMap<Integer, HaltingPointInstance>> haltingPointInstancesByTimeStamps = new ArrayList<>();

        // Ready output file bases
        String mobileVehicleInstancesOutputFilePathsBase = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "MobileVehicleInstances_11.2024/MobileVehicleInstances";
        String haltingPointInstancesOutputFilePathsBase = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "HaltingPointInstances_12.2024/HaltingPointInstances";
        int counter = 0;    // To number files by date-time stamps

        // Build sequences of mobileVehicle- and haltingPointInstances on highways
        for (long secondsValueForInstanceBuilding : secondsForBuildingInstances) {
            // Set up file paths
            counter++;
            String mobileVehicleInstancesFilePath = mobileVehicleInstancesOutputFilePathsBase + counter + ".csv";
            String haltingPointInstancesFilePath = haltingPointInstancesOutputFilePathsBase + counter + ".csv";

            /* Generate a map of mobile vehicle instances for the time stamp under consideration, add them to the
               master list, and write them out into a CSV
            */
            LinkedHashMap<String, MobileVehicleInstance> mobileVehicleInstances = generateMobileVehicles
                    (plazaLaneWiseVehicleTollingInstances, secondsValueForInstanceBuilding, secondsForBuildingInstances,
                            distanceVsCoordinatesMapFromDel, distanceVsCoordinatesMapFromAgr,
                            mobileVehicleInstancesByTimeStamps, haltingPoints);
            mobileVehicleInstancesByTimeStamps.add(mobileVehicleInstances);
            writeVehicleInstanceData(mobileVehicleInstancesFilePath, mobileVehicleInstances);

//            /* Generate a map of mobile vehicle instances for the time stamp under consideration, add them to the
//               master list, and write them out into a CSV
//            */
//            LinkedHashMap<String, MobileVehicleInstance> mobileVehicleInstances = generateMobileVehicles
//                    (plazaLaneWiseVehicleTollingInstances, secondsValueForInstanceBuilding, secondsForBuildingInstances,
//                            distanceVsCoordinatesMapFromDel, distanceVsCoordinatesMapFromAgr,
//                            mobileVehicleInstancesByTimeStamps);
//            mobileVehicleInstancesByTimeStamps.add(mobileVehicleInstances);
//            writeVehicleInstanceData(mobileVehicleInstancesFilePath, mobileVehicleInstances);
        }

        System.exit(1);
        java.awt.Toolkit.getDefaultToolkit().beep();

        String haltingPointDataFilePath = "D:/Documents - Education + Work/Assignment - CEEW/Data/" +
                "haltingPointsNH44.csv";
        haltingPointReaderWriter.writeHaltingPointData(haltingPointDataFilePath);
    }

    private static void writeVehicleInstanceData(String vehicleInstanceDataFilePath,
                                                 LinkedHashMap<String, MobileVehicleInstance>
                                                         mobileVehicleInstances) {
        try {
            // Writer for a CSV file containing vehicular coordinates and other information
            BufferedWriter vehicleInstanceWriter = new BufferedWriter(new FileWriter(vehicleInstanceDataFilePath));

            // Write out the header
            vehicleInstanceWriter.write("VehRegNo,VehClass,isEV,AscribedSpeed_ms,ActualSpeed_ms,DidVehicleHalt," +
                    "HaltingDuration_secs,BatteryCapacity_kWh,FuelEconomy_kWhPerKm,StartingSoC_percent," +
                    "CurrentSoC_percent,EnergyRequired_kWh,ChargingProbability,EstimatedArrivalTime_s,PlazaName," +
                    "LaneNo,TravelDirection,TransactionTime_s,PrecVehTransactionTime_s,RankInQueue,TimeAtPlaza_s," +
                    "DistFromDestAtStipTime_m,AscribedXCoordinateStipTime,AscribedYCoordinateStipTime," +
                    "NearestHaltingPointInstance\n");

            // Write out data
            for (MobileVehicleInstance mobileVehicleInstance : mobileVehicleInstances.values()) {
                vehicleInstanceWriter.write(
                        mobileVehicleInstance.getVehicleRegistration() + "," +
                                mobileVehicleInstance.getEmployeeSpecifiedVehicleClass() + "," +
                                mobileVehicleInstance.getIsEV() + "," +
                                mobileVehicleInstance.getAssignedVehicleSpeedMPS() + "," +
                                mobileVehicleInstance.getActualVehicleSpeedMPS() + "," +
                                mobileVehicleInstance.getDidVehicleHalt() + "," +
                                mobileVehicleInstance.getHaltingDurationSecs() + "," +
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
                                mobileVehicleInstance.getDistanceFromOriginAtStipulatedTime() + "," +
                                mobileVehicleInstance.getAscribedXCoordinateAtStipulatedTime() + "," +
                                mobileVehicleInstance.getAscribedYCoordinateAtStipulatedTime() + "," +
                                (mobileVehicleInstance.getNearestHaltingPointInstance() != null
                                        ? mobileVehicleInstance.getNearestHaltingPointInstance().toString() :
                                        "None") + "\n");
            }

            vehicleInstanceWriter.close();
            System.out.println("Data of mobile vehicle instances written to " + vehicleInstanceDataFilePath);

        } catch (IOException iOE) {
            System.out.println("Input-output exception; please check the hashmap of mobile vehicle instances.");
        }
    }

    private static void writeHaltingPointInstances(String filePath,
                                                   List<HaltingPointInstance> haltingPointInstances) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            // Write the header
            writer.write("HaltingPointId,HaltingPointDescription,Latitude,Longitude,Category,SubCategory," +
                    "DataCollectionTime,ChargerDowntime,ChargerCapacityUtilization,ChargerCount,TotalChargerOutputKW," +
                    "AggregateChargingDemandKWh,AverageChargingProbability,AverageHaltingDuration," +
                    "PassengerVehicleCount,FreightVehicleCount,PowerOutputRequired\n");

            // Write the data
            for (HaltingPointInstance instance : haltingPointInstances) {
                writer.write(
                        instance.getHaltingPointId() + "," +
                                instance.getHaltingPointDescription() + "," +
                                instance.getHaltingPointLatitude() + "," +
                                instance.getHaltingPointLongitude() + "," +
                                instance.getHaltingPointCategory() + "," +
                                instance.getHaltingPointSubCategory() + "," +
                                instance.getHaltingPointDataCollectionTime() + "," +
                                instance.getChargerDowntime() + "," +
                                instance.getChargerCapacityUtilization() + "," +
                                instance.getChargerCount() + "," +
                                instance.getTotalChargerOutputKW() + "," +
                                instance.getAggregateChargingDemandKWh() + "," +
                                instance.getAverageVehicleChargingProbability() + "," +
                                instance.getAverageVehicleHaltingDuration() + "," +
                                instance.getPassengerVehicleCountDesiringCharge() + "," +
                                instance.getFreightVehicleCountDesiringCharge() + "," +
                                instance.getPowerOutputRequired() + "\n"
                );
            }
            System.out.println("Data of halting points written to " + filePath);

        } catch (IOException e) {
            System.err.println("Error writing halting point instances to file: " + e.getMessage());
        }
    }

    private static LinkedHashMap<String, MobileVehicleInstance> generateMobileVehicles(
            LinkedHashMap<String, LinkedHashMap<Integer, LinkedHashMap<Long, VehicleTollingInstance>>>
                    plazaLaneWiseTollingInstanceMaps, long secondsValueForBuildingInstances,
            ArrayList<Long> secondsForBuildingInstances,
            LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromDel,
            LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromAgr,
            ArrayList<LinkedHashMap<String, MobileVehicleInstance>> mobileVehicleInstancesByTimeStamps,
            List<HaltingPointInstance> haltingPoints) {

        // Setting up two maps of mobile vehicle instances for stability incorporation
        LinkedHashMap<String, MobileVehicleInstance> mobileVehicleInstanceMap = new LinkedHashMap<>();
        LinkedHashMap<String, MobileVehicleInstance> precedingMobileVehicleInstanceMap = new LinkedHashMap<>();
        // String keys refer to vehicle registration numbers, and values refer to MobileVehicleInstance objects

        if (!mobileVehicleInstancesByTimeStamps.isEmpty()) {
            precedingMobileVehicleInstanceMap = mobileVehicleInstancesByTimeStamps.
                    get(mobileVehicleInstancesByTimeStamps.size() - 1);
        }

        for (String tollPlazaName : plazaLaneWiseTollingInstanceMaps.keySet()) {
            LinkedHashMap<Integer, LinkedHashMap<Long, VehicleTollingInstance>>
                    plazaSpecificLaneWiseTollingInstanceMaps = plazaLaneWiseTollingInstanceMaps.get(tollPlazaName);

            for (int laneNumber : plazaSpecificLaneWiseTollingInstanceMaps.keySet()) {
                LinkedHashMap<Long, VehicleTollingInstance> plazaLaneSpecificTollingInstanceMap =
                        plazaSpecificLaneWiseTollingInstanceMaps.get(laneNumber);
                // Reset each time lanes or plazas shift
                long precedingVehicleTransactionTimeSeconds = 0;
                int rankInQueue = 1;
                double timeSpentAtPlazaInSeconds;

                // Assigned a default pre-processing value where required
                boolean isEV;
                double assignedVehicleSpeedMPS;
                double actualVehicleSpeedMPS;
                boolean didVehicleHalt = false;
                double haltingDurationMins = 0;

                double vehicleBatteryCapacityKWh = 0;
                double vehicleFuelEconomyKWhPKm = 0;
                double vehicleStartingSoCPercent = 0;
                double vehicleCurrentSoCLevelPercent = 0;
                double vehicleEnergyRequired = 0;
                double vehicleChargingProbability = 0;

                for (HashMap.Entry<Long, VehicleTollingInstance> vehicleTollingInstanceEntry :
                        plazaLaneSpecificTollingInstanceMap.entrySet()) {
                    VehicleTollingInstance vehicleTollingInstance = vehicleTollingInstanceEntry.getValue();
                    long transactionTimeSeconds = vehicleTollingInstance.getTransactionTimeSeconds();

                    /* Setting up a temporal filter here. If the tolling transaction took place eight hours before or
                       after the date and time stamp for building the highway instance, then it is discarded. This is
                       done to ensure that not all 1.6 million records are considered for every JTDA run or highway
                       instance build.
                    */
                    // System.out.println(secondsValueForBuildingInstances - vehicleTollingInstance.getTransactionTimeSeconds());
                    if (Math.abs(secondsValueForBuildingInstances - vehicleTollingInstance.getTransactionTimeSeconds())
                            <= 14_400) {
                        String vehicleRegistration = vehicleTollingInstance.getVehicleRegistration();
                        String employeeSpecifiedVehicleClass = vehicleTollingInstance.
                                getEmployeeSpecifiedVehicleClass();

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

                        // Vehicle was present on the highway in the previous instance also
                        if ((!precedingMobileVehicleInstanceMap.isEmpty()) && (precedingMobileVehicleInstanceMap.
                                containsKey(vehicleRegistration))) {
                            MobileVehicleInstance precedingMobileVehicleInstance = precedingMobileVehicleInstanceMap.
                                    get(vehicleRegistration);

                            isEV = precedingMobileVehicleInstance.getIsEV();
                            assignedVehicleSpeedMPS = precedingMobileVehicleInstance.getAssignedVehicleSpeedMPS();
                            actualVehicleSpeedMPS = precedingMobileVehicleInstance.getActualVehicleSpeedMPS();
                            didVehicleHalt = precedingMobileVehicleInstance.getDidVehicleHalt();
                            haltingDurationMins = precedingMobileVehicleInstance.getHaltingDurationSecs();
                            precedingVehicleTransactionTimeSeconds = precedingMobileVehicleInstance.
                                    getPrecedingVehicleTransactionTimeSeconds();
                            rankInQueue = precedingMobileVehicleInstance.getRankInQueue();
                            timeSpentAtPlazaInSeconds = precedingMobileVehicleInstance.getTimeSpentAtPlazaInSeconds();

                            if (isEV) {
                                vehicleBatteryCapacityKWh = precedingMobileVehicleInstance.
                                        getVehicleBatteryCapacityKWh();
                                vehicleFuelEconomyKWhPKm = precedingMobileVehicleInstance.getVehicleFuelEconomyKWhPKm();
                                vehicleStartingSoCPercent = precedingMobileVehicleInstance.
                                        getVehicleStartingSoCPercent();

                                long temporalGapSeconds = secondsForBuildingInstances.get(
                                        mobileVehicleInstancesByTimeStamps.size() - 1) -
                                        secondsValueForBuildingInstances;

                                // Handle negatives here if needed
                                vehicleCurrentSoCLevelPercent = 95 * (vehicleFuelEconomyKWhPKm / 1_000) *
                                        (assignedVehicleSpeedMPS * temporalGapSeconds) / vehicleBatteryCapacityKWh;
                                vehicleEnergyRequired = vehicleBatteryCapacityKWh * (0.95 -
                                        (vehicleCurrentSoCLevelPercent / 100)); // 95% is the desired and healthy SoC
                                vehicleChargingProbability = vehicleEnergyRequired / vehicleBatteryCapacityKWh;
                            }
                        } else {
                            assignedVehicleSpeedMPS = assignSpeed(employeeSpecifiedVehicleClass);
                            actualVehicleSpeedMPS = assignedVehicleSpeedMPS;    // If a vehicle cannot be gauged to halt

                            Random haltingRandom = new Random();
                            int haltingDecider = haltingRandom.nextInt(5);
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

                                haltingDurationMins = (int) ((distanceToUseForHaltingDuration / actualVehicleSpeedMPS) -
                                        (distanceToUseForHaltingDuration / assignedVehicleSpeedMPS));
                            }

                            isEV = makeEVOrNot(employeeSpecifiedVehicleClass);
                            if (isEV) {
                                vehicleBatteryCapacityKWh = assignBatteryCapacity(employeeSpecifiedVehicleClass);
                                vehicleFuelEconomyKWhPKm = assignFuelEconomy(employeeSpecifiedVehicleClass);
                                vehicleStartingSoCPercent = assignStartingSoCLevel(employeeSpecifiedVehicleClass,
                                        tollPlazaName, directionId);
                            }

                            /**
                             * Set sensitivity of queue-formation using transaction time gaps below at the Boolean gates
                             */
                            if ((transactionTimeSeconds - precedingVehicleTransactionTimeSeconds) <= 10) {
                                rankInQueue += 1;
                                timeSpentAtPlazaInSeconds = (assignedVehicleSpeedMPS / AVERAGE_DECELERATION_RATE_MS2) *
                                        2 + TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS +
                                        TIME_TAKEN_QUEUED_TOLL_PROCESSING_SECONDS * (rankInQueue - 1);
                            } else {
                                rankInQueue = 1;
                                timeSpentAtPlazaInSeconds = (assignedVehicleSpeedMPS / AVERAGE_DECELERATION_RATE_MS2) *
                                        2 + TIME_TAKEN_FREE_FLOW_TOLL_PROCESSING_SECONDS;
                            }
                        }

                        long estimatedArrivalTimeSeconds = transactionTimeSeconds - (long) timeSpentAtPlazaInSeconds;

                        int distanceFromOriginAtStipulatedTime = 0;
                        double ascribedXCoordinateAtStipulatedTime = 0;
                        double ascribedYCoordinateAtStipulatedTime = 0;

                        MobileVehicleInstance mobileVehicleInstance = new MobileVehicleInstance(vehicleRegistration,
                                employeeSpecifiedVehicleClass, isEV, assignedVehicleSpeedMPS, actualVehicleSpeedMPS,
                                didVehicleHalt, haltingDurationMins, vehicleBatteryCapacityKWh,
                                vehicleFuelEconomyKWhPKm, vehicleStartingSoCPercent, vehicleCurrentSoCLevelPercent,
                                vehicleEnergyRequired, vehicleChargingProbability, tollPlazaName, laneNumber,
                                directionId, transactionTimeSeconds, estimatedArrivalTimeSeconds,
                                precedingVehicleTransactionTimeSeconds, rankInQueue, timeSpentAtPlazaInSeconds,
                                distanceFromOriginAtStipulatedTime, ascribedXCoordinateAtStipulatedTime,
                                ascribedYCoordinateAtStipulatedTime);

                        distanceFromOriginAtStipulatedTime = assignHighwayDistanceM(mobileVehicleInstance,
                                secondsValueForBuildingInstances);
                        mobileVehicleInstance.setDistanceFromOriginAtStipulatedTime(distanceFromOriginAtStipulatedTime);

                        // Ensuring that all vehicle instances are present on the concerned highway section
                        if ((distanceFromOriginAtStipulatedTime >= 0) && (distanceFromOriginAtStipulatedTime <=
                                DELHI_TO_AGRA_DIST_M)) {
                            if (directionId.equalsIgnoreCase("DA")) {
                                CoordinatesDistances locationForDADirection = distanceVsCoordinatesMapFromDel.get(
                                        distanceFromOriginAtStipulatedTime);
                                if (locationForDADirection != null) {
                                    mobileVehicleInstance.setAscribedXCoordinateAtStipulatedTime(locationForDADirection.
                                            getXCoordinate());
                                    mobileVehicleInstance.setAscribedYCoordinateAtStipulatedTime(locationForDADirection.
                                            getYCoordinate());
                                    /* Debugging statements:
                                    System.out.println("X: " + locationForDADirection.getXCoordinate());
                                    System.out.println("Y: " + locationForDADirection.getYCoordinate());
                                    */
                                }
                            } else if (directionId.equalsIgnoreCase("AD")) {
                                CoordinatesDistances locationForADDirection = distanceVsCoordinatesMapFromAgr.get(
                                        distanceFromOriginAtStipulatedTime);
                                if (locationForADDirection != null) {
                                    mobileVehicleInstance.setAscribedXCoordinateAtStipulatedTime(locationForADDirection.
                                            getXCoordinate());
                                    mobileVehicleInstance.setAscribedYCoordinateAtStipulatedTime(locationForADDirection.
                                            getYCoordinate());
                                    /* Debugging statements:
                                    System.out.println("X: " + locationForADDirection.getXCoordinate());
                                    System.out.println("Y: " + locationForADDirection.getYCoordinate());
                                    */
                                }
                            }

                            if (isEV) {
                                vehicleCurrentSoCLevelPercent = assignCurrentSoCLevel(vehicleStartingSoCPercent,
                                        vehicleFuelEconomyKWhPKm, vehicleBatteryCapacityKWh,
                                        distanceFromOriginAtStipulatedTime);
                                vehicleEnergyRequired = getVehicleEnergyRequired(vehicleCurrentSoCLevelPercent,
                                        vehicleBatteryCapacityKWh);
                                vehicleChargingProbability = vehicleEnergyRequired / vehicleBatteryCapacityKWh;
                                // System.out.println(vehicleCurrentSoCLevelPercent);
                                // System.out.println(vehicleEnergyRequired + employeeSpecifiedVehicleClass);
                                // System.out.println(vehicleChargingProbability);
                            }

                            mobileVehicleInstance.setVehicleCurrentSoCLevelPercent(vehicleCurrentSoCLevelPercent);
                            mobileVehicleInstance.setVehicleEnergyRequired(vehicleEnergyRequired);
                            mobileVehicleInstance.setVehicleChargingProbability(vehicleChargingProbability);

                            // Find the nearest halting point
                            mobileVehicleInstance.setNearestHaltingPointInstance(findNearestHaltingPoint
                                    (mobileVehicleInstance, haltingPoints));
                            // System.out.println(mobileVehicleInstance.getNearestHaltingPointInstance().
                            // getHaltingPointDescription());

                            // Add the mobile vehicle instance to the relevant map
                            mobileVehicleInstanceMap.put(vehicleRegistration, mobileVehicleInstance);
                        }

                        /* Debug: Check data records subject to conditionality
                        if ((distanceFromOriginAtStipulatedTime >= 0) && (distanceFromOriginAtStipulatedTime <=
                        DELHI_TO_AGRA_DIST_M)) {
                            System.out.println("Mobile vehicle instance details: " + "\n" +
                                    "Vehicle registration: " + vehicleRegistration + "\n" +
                                    "Employee specified vehicle class: " + employeeSpecifiedVehicleClass + "\n" +
                                    "Is EV: " + isEV + "\n" +
                                    "Assigned vehicle speed (m/s): " + assignedVehicleSpeedMPS + "\n" +
                                    "Actual vehicle speed (m/s): " + actualVehicleSpeedMPS + "\n" +
                                    "Did vehicle halt: " + didVehicleHalt + "\n" +
                                    "Halting duration (mins): " + haltingDurationMins + "\n" +
                                    "Vehicle battery capacity (kWh): " + vehicleBatteryCapacityKWh + "\n" +
                                    "Vehicle fuel economy (kWh/km): " + vehicleFuelEconomyKWhPKm + "\n" +
                                    "Vehicle starting SoC (%): " + vehicleStartingSoCPercent + "\n" +
                                    "Vehicle current SoC level (%): " + vehicleCurrentSoCLevelPercent + "\n" +
                                    "Vehicle energy required (kWh): " + vehicleEnergyRequired + "\n" +
                                    "Vehicle charging probability: " + vehicleChargingProbability + "\n" +
                                    "Toll plaza name: " + tollPlaza + "\n" +
                                    "Lane number: " + laneNumber + "\n" +
                                    "Direction of motion: " + directionId + "\n" +
                                    "Transaction time (in seconds): " + transactionTimeSeconds + "\n" +
                                    "Estimated arrival time (in seconds): " + estimatedArrivalTimeSeconds + "\n" +
                                    "Preceding vehicle transaction time (in seconds): " +
                                    precedingVehicleTransactionTimeSeconds + "\n" +
                                    "Rank in queue: " + rankInQueue + "\n" +
                                    "Time spent at plaza (in seconds): " + timeSpentAtPlazaInSeconds + "\n" +
                                    "Distance from origin at stipulated time (m): " + distanceFromOriginAtStipulatedTime
                                    + "\n" +
                                    "Ascribed x-coordinate at stipulated time: " + ascribedXCoordinateAtStipulatedTime +
                                    "\n" +
                                    "Ascribed y-coordinate at stipulated time: " + ascribedYCoordinateAtStipulatedTime +
                                    "\n" +
                                    "Nearest halting point instance: " + (nearestHaltingPointInstance != null ?
                                    nearestHaltingPointInstance.toString() : "None") + "\n");
                        }

                    */

                    }
                    precedingVehicleTransactionTimeSeconds = transactionTimeSeconds;
                }
            }
        }

        return mobileVehicleInstanceMap;
    }

    // todo build a method that takes halting points, and a set of temporal points, and a set of mob vehicle instances, then
    // for each timepoint, it calls the set of vehicle instances, sees which halting point is the nearest for each vehicle
    // and returns an aggregate metric for the halting point
    // will be nice if we can get a day-wide metric for each halting point

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
            case "CarJeep" -> {if (eVDeciderValue < eVPenetrationRateCarJeep) {isEV = true;}}
            case "LCV" -> {if (eVDeciderValue < eVPenetrationRateLCV) {isEV = true;}}
            case "Truck" -> {if (eVDeciderValue < eVPenetrationRateTruck) {isEV = true;}}
            case "Bus" -> {if (eVDeciderValue < eVPenetrationRateBus) {isEV = true;}}
            case "MAV" -> {if (eVDeciderValue < eVPenetrationRateMAV) {isEV = true;}}
            case "Tractor" -> {if (eVDeciderValue < eVPenetrationRateTractor) {isEV = true;}}
        }

        return isEV;
    }

    private static int assignHighwayDistanceM (MobileVehicleInstance mobileVehicleInstance,
                                       long secondsValueForBuildingInstances) {
        String directionId = mobileVehicleInstance.getDirectionId();
        String plazaName = mobileVehicleInstance.getTollPlaza();
        double vehicleSpeedMPS = mobileVehicleInstance.getAssignedVehicleSpeedMPS();

        double timeToBackTrackInSeconds = (mobileVehicleInstance.getTransactionTimeSeconds() - mobileVehicleInstance.
                getTimeSpentAtPlazaInSeconds()) - secondsValueForBuildingInstances;
        /* A vehicle instance is yet to arrive at the plaza if the above quantity is positive, and has already passed it
        if it is negative
        */

        /* todo use Debugging statements:
        System.out.println(mobileVehicleInstance.getTransactionTimeSeconds());
        System.out.println(mobileVehicleInstance.getTimeSpentAtPlazaInSeconds());
        System.out.println(secondsValueForVisualizingVehicleLocations);
        System.out.println("Vehicle speed " + vehicleSpeedMPS);
        System.out.println("Time to backtrack " + timeToBackTrackInSeconds);
        System.out.println(backtrackingDistanceM);
        */

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

        // System.out.println("Distance along highway: " + distanceAlongHighway);   // Debugging statement
        return distanceAlongHighway;
    }

    private static double assignCurrentSoCLevel(double startingSoCLevel, double fuelEconomy, double batteryCapacity,
                                             int distanceTravelledM) {
        double criticalSoCLevel = 10;
        double maxSoCLevel = 95;
        double mPerKm = 1_000;
        double vehicleRange = 0.95 * batteryCapacity / fuelEconomy;

        double distancePossibleViaStartingSoCLevel = (startingSoCLevel - criticalSoCLevel) / maxSoCLevel * vehicleRange
                * mPerKm;
        double distancePossibleViaMaxSoCLevel = (maxSoCLevel - criticalSoCLevel) / maxSoCLevel * vehicleRange * mPerKm;
        double currentSoCLevel = 50;

        if (distanceTravelledM < distancePossibleViaStartingSoCLevel) {
            currentSoCLevel = startingSoCLevel * (1 - (distanceTravelledM / distancePossibleViaStartingSoCLevel));
        } else {
            double selfManagedDistanceM = distanceTravelledM - distancePossibleViaStartingSoCLevel;
            int numberOfSelfManagedCycles = (int) (selfManagedDistanceM / distancePossibleViaMaxSoCLevel);
            double distanceOnCurrentCycle = selfManagedDistanceM - (numberOfSelfManagedCycles *
                    distancePossibleViaMaxSoCLevel);
            currentSoCLevel = maxSoCLevel * (1 - (distanceOnCurrentCycle / distancePossibleViaMaxSoCLevel));
        }
        currentSoCLevel = Math.max(currentSoCLevel, criticalSoCLevel);
        currentSoCLevel = Math.min(currentSoCLevel, maxSoCLevel);

        /* Debugging code:
        System.out.println("Travelled distance: " + distanceTravelledM);
        System.out.println("Per cycle distance: " + distanceTravelledPerVehicleCycleM);
        System.out.println("Number of self managed cycles: " + numberOfSelfManagedCycles);
        System.out.println("Starting SoC level: " + startingSoCLevel);
        System.out.println("Current SoC level: " + currentSoCLevel);
        */

        return currentSoCLevel;
    }

    private static double getVehicleEnergyRequired(double currentSoCLevel, double batteryCapacity) {
        double maxSoCLevel = 95;
        return batteryCapacity * (maxSoCLevel - currentSoCLevel) / 100;
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

    private static double calculateSpeed(String SVCClass) {
        double averageVehicleClassSpeed = 15.44;
        double stdDevVehicleClassSpeed = 1.77;

        switch (SVCClass) {
            case "CarJeep" -> {averageVehicleClassSpeed = 14.37; stdDevVehicleClassSpeed = 0.91;}
            case "LCV" -> {averageVehicleClassSpeed = 12.62; stdDevVehicleClassSpeed = 0.72;}
            case "Truck" -> {averageVehicleClassSpeed = 10.18; stdDevVehicleClassSpeed = 0.58;}
            case "Bus" -> {averageVehicleClassSpeed = 11.04; stdDevVehicleClassSpeed = 0.24;}
            case "MAV" -> {averageVehicleClassSpeed = 8.07; stdDevVehicleClassSpeed = 0.29;}
            case "Tractor" -> {averageVehicleClassSpeed = 5.88; stdDevVehicleClassSpeed = 0.12;}
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
            case "Truck" -> {averageVehicleBatteryCapacity = 78;}
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
                    case "LCV", "Tractor" -> {stdDevStartingSoCLevel = 6;}
                    case "Truck", "MAV" -> {averageStartingSoCLevel = 57;}
                    case "Bus" -> {averageStartingSoCLevel = 90; stdDevStartingSoCLevel = 2;}
                }
            } else {
                switch (SVCClass) {
                    case "CarJeep" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 7;}
                    case "LCV" -> {averageStartingSoCLevel = 28; stdDevStartingSoCLevel = 5;}
                    case "Truck", "MAV" -> {averageStartingSoCLevel = 37;}
                    case "Bus" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 2;}
                    case "Tractor" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 6;}
                }
            }
        } else if (tollPlazaName.equalsIgnoreCase("Karman")) {
            switch (SVCClass) {
                case "CarJeep" -> {
                    stdDevStartingSoCLevel = 7;}
                case "LCV" -> {averageStartingSoCLevel = 39; stdDevStartingSoCLevel = 6;}
                case "Truck" -> {averageStartingSoCLevel = 47;}
                case "Bus" -> {averageStartingSoCLevel = 60; stdDevStartingSoCLevel = 6;}
                case "MAV" -> {averageStartingSoCLevel = 47; stdDevStartingSoCLevel = 8;}
                case "Tractor" -> {averageStartingSoCLevel = 40; stdDevStartingSoCLevel = 6;}
            }
        } else if (tollPlazaName.equalsIgnoreCase("Mahuvan")) {
            if (directionId.equalsIgnoreCase("DA")) {
                switch (SVCClass) {
                    case "CarJeep" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 7;}
                    case "LCV" -> {averageStartingSoCLevel = 28; stdDevStartingSoCLevel = 5;}
                    case "Truck", "MAV" -> {averageStartingSoCLevel = 37;}
                    case "Bus" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 2;}
                    case "Tractor" -> {averageStartingSoCLevel = 30; stdDevStartingSoCLevel = 6;}
                }
            } else {
                switch (SVCClass) {
                    case "CarJeep" -> {averageStartingSoCLevel = 70; stdDevStartingSoCLevel = 7;}
                    case "LCV", "Tractor" -> {stdDevStartingSoCLevel = 6;}
                    case "Truck", "MAV" -> {averageStartingSoCLevel = 57;}
                    case "Bus" -> {averageStartingSoCLevel = 90; stdDevStartingSoCLevel = 2;}
                }
            }
        }

        Random startingSoCLevelRandom = new Random();
        return startingSoCLevelRandom.nextGaussian(averageStartingSoCLevel, stdDevStartingSoCLevel);
    }

    public static HaltingPointInstance findNearestHaltingPoint(
            MobileVehicleInstance vehicle, List<HaltingPointInstance> haltingPoints) {
        KDTree kdTree = new KDTree(haltingPoints);

        HaltingPointInstance targetPoint = new HaltingPointInstance(
                -1, "Vehicle Position",
                vehicle.getAscribedXCoordinateAtStipulatedTime(), vehicle.getAscribedYCoordinateAtStipulatedTime(),
                null, null, null, 0,
                0, 0, 0);

        return kdTree.findNearest(targetPoint);
    }
}