package HaltingPointDataParser;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class HaltingPointReaderWriter {
    private LinkedHashMap<Integer, HaltingPointInstance> haltingPoints = new LinkedHashMap<>();

    public void readPoIData(String poIDataFilePath) {
        try {
            // Create a reader for PoI data
            BufferedReader poIDataReader = new BufferedReader(new FileReader(poIDataFilePath));
            String newline;

            // Read the headers and determine the indices
            String[] poIDataHeaderArray = poIDataReader.readLine().split(",");
            // System.out.println(Arrays.toString(poIDataHeaderArray));  // To debug
            int haltingPointIdIndex = findIndexInArray("poi_id", poIDataHeaderArray);
            int haltingPointDescriptionIndex = findIndexInArray("poi_description", poIDataHeaderArray);
            int haltingPointLatitudeIndex = findIndexInArray("latitude", poIDataHeaderArray);
            int haltingPointLongitudeIndex = findIndexInArray("longitude", poIDataHeaderArray);
            int haltingPointCategoryIndex = findIndexInArray("category", poIDataHeaderArray);
            int haltingPointSubCategoryIndex = findIndexInArray("sub_category", poIDataHeaderArray);
            int haltingPointDataCollectionTimeIndex = findIndexInArray("data_collection_time",
                    poIDataHeaderArray);
            int chargerDowntimeIndex = findIndexInArray("Average Charger Downtime", poIDataHeaderArray);
            int chargerCapacityUtilizationIndex = findIndexInArray("Charging Capacity Utilization (%)",
                    poIDataHeaderArray);
            int chargerCountIndex = findIndexInArray("Charger Count", poIDataHeaderArray);
            int totalChargerOutputKWIndex = findIndexInArray("Total Charger Output (kW)",
                    poIDataHeaderArray);

            // Read data records
            while ((newline = poIDataReader.readLine()) != null) {
                newline = newline.replaceAll("\"", "");
                String[] poIDataRecord = newline.split(",");
                // System.out.println(Arrays.toString(poIDataRecord));  // To debug

                int haltingPointId = Integer.parseInt(poIDataRecord[haltingPointIdIndex]);
                String haltingPointDescription = poIDataRecord[haltingPointDescriptionIndex];
                double haltingPointLatitude = Double.parseDouble(poIDataRecord[haltingPointLatitudeIndex]);
                double haltingPointLongitude = Double.parseDouble(poIDataRecord[haltingPointLongitudeIndex]);
                String haltingPointCategory = poIDataRecord[haltingPointCategoryIndex];
                String haltingPointSubCategory = poIDataRecord[haltingPointSubCategoryIndex];
                String haltingPointDataCollectionTime = poIDataRecord[haltingPointDataCollectionTimeIndex];
                double chargerDowntime = Double.parseDouble(poIDataRecord[chargerDowntimeIndex]);
                double chargerCapacityUtilization = Double.parseDouble(poIDataRecord[chargerCapacityUtilizationIndex]);
                double chargerCount = Double.parseDouble(poIDataRecord[chargerCountIndex]);
                double totalChargerOutputKW = Double.parseDouble(poIDataRecord[totalChargerOutputKWIndex]);

                /* Use only if needed:
                double aggregateDailyChargingDemandKWh = 0;
                double averageVehicleChargingProbability = 0;
                double averageVehicleHaltingDuration = 0;
                double averagePassengerVehicleCountDesiringCharge = 0;
                double averageFreightVehicleCountDesiringCharge = 0;
                double powerOutputRequired = 0;
                */

                HaltingPointInstance haltingPointInstance = new HaltingPointInstance(haltingPointId,
                        haltingPointDescription, haltingPointLatitude, haltingPointLongitude, haltingPointCategory,
                        haltingPointSubCategory, haltingPointDataCollectionTime, chargerDowntime,
                        chargerCapacityUtilization, chargerCount, totalChargerOutputKW);
                this.haltingPoints.put(haltingPointId, haltingPointInstance);
            }
            System.out.println("Halting points mapped from " + poIDataFilePath);

        } catch (FileNotFoundException fNFE) {
            System.out.println("File not found at the specified file path: " + poIDataFilePath);
        } catch (IOException iOE) {
            System.out.println("Input-output exception; please check the file at " + poIDataFilePath);
        }
    }

    public void writeHaltingPointData(String haltingPointDataFilePath) {
        try {
            // Writer for halting points' data
            BufferedWriter haltingPointsDataWriter = new BufferedWriter(new FileWriter(haltingPointDataFilePath));

            haltingPointsDataWriter.close();
            // Set up a header array
            haltingPointsDataWriter.write("haltingPointId,haltingPointDescription,haltingPointLatitude," +
                    "haltingPointLongitude,haltingPointCategory,haltingPointSubCategory," +
                            "haltingPointDataCollectionTime,chargerDowntime,chargerCapacityUtilization,chargerCount," +
                    "totalChargerOutputkW,aggregateDailyChargingDemandkWh,averageVehicleChargingProbability," +
                    "averageVehicleHaltingDurationMin,averagePassengerVehicleCountDesiringCharge," +
                    "averageFreightVehicleCountDesiringCharge,powerOutputRequired");

            // Write body based on "haltingPoints" hashmap
            for (HashMap.Entry<Integer, HaltingPointInstance> haltingPointInstanceEntry : this.haltingPoints.
                    entrySet()) {
                HaltingPointInstance haltingPointInstance = haltingPointInstanceEntry.getValue();

                int haltingPointId = haltingPointInstance.getHaltingPointId();
                String haltingPointDescription = haltingPointInstance.getHaltingPointDescription();
                double haltingPointLatitude = haltingPointInstance.getHaltingPointLatitude();
                double haltingPointLongitude = haltingPointInstance.getHaltingPointLongitude();
                String haltingPointCategory = haltingPointInstance.getHaltingPointCategory();
                String haltingPointSubCategory = haltingPointInstance.getHaltingPointSubCategory();
                String haltingPointDataCollectionTime = haltingPointInstance.getHaltingPointDataCollectionTime();
                double chargerDowntime = haltingPointInstance.getChargerDowntime();
                double chargerCapacityUtilization = haltingPointInstance.getChargerCapacityUtilization();
                double chargerCount = haltingPointInstance.getChargerCount();
                double totalChargerOutputKW = haltingPointInstance.getTotalChargerOutputKW();
                double aggregateDailyChargingDemandKWh = haltingPointInstance.getAggregateChargingDemandKWh();
                double averageVehicleChargingProbability = haltingPointInstance.getAverageVehicleChargingProbability();
                double averageVehicleHaltingDuration = haltingPointInstance.getAverageVehicleHaltingDuration();
                double averagePassengerVehicleCountDesiringCharge = haltingPointInstance.
                        getPassengerVehicleCountDesiringCharge();
                double averageFreightVehicleCountDesiringCharge = haltingPointInstance.
                        getFreightVehicleCountDesiringCharge();
                double powerOutputRequired = haltingPointInstance.getPowerOutputRequired();

                haltingPointsDataWriter.write(haltingPointId + "," +
                        haltingPointDescription + "," +
                        haltingPointLatitude + "," +
                        haltingPointLongitude + "," +
                        haltingPointCategory + "," +
                        haltingPointSubCategory + "," +
                        haltingPointDataCollectionTime + "," +
                        chargerDowntime + "," +
                        chargerCapacityUtilization + "," +
                        chargerCount + "," +
                        totalChargerOutputKW + "," +
                        aggregateDailyChargingDemandKWh + "," +
                        averageVehicleChargingProbability + "," +
                        averageVehicleHaltingDuration + "," +
                        averagePassengerVehicleCountDesiringCharge + "," +
                        averageFreightVehicleCountDesiringCharge + "," +
                        powerOutputRequired);
            }

            haltingPointsDataWriter.close();
            System.out.println("Halting points' data written to " + haltingPointDataFilePath);

        } catch (IOException iOE) {
            System.out.println("Input-output exception; please check the hashmap of haling points");
        }
    }

    private static int findIndexInArray(String columnName, String[] columnHeaders) {
        int columnIndex = -1;
        for (int i = 0; i < columnHeaders.length; i++) {
            // Address Byte Order Mark characters (unprintable), if present
            columnHeaders[i] = columnHeaders[i].replaceAll("\"", "");

            if (columnHeaders[i].equalsIgnoreCase(columnName)) {
                columnIndex = i;
                break;
            }
        }

        return columnIndex;
    }

    public LinkedHashMap<Integer, HaltingPointInstance> getHaltingPoints() {
        return this.haltingPoints;
    }
}
