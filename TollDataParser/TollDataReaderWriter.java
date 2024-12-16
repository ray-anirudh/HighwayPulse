import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;

public class TollDataReaderWriter {
     // Functionality on point - 16.12.2024

     /**
      * Attribute definitions
      */
     private LinkedHashMap<String, LinkedHashMap<Integer, LinkedHashMap<Long, VehicleTollingInstance>>>
             plazaLaneWiseTollingInstanceMaps = new LinkedHashMap<>();
     /* In the above hashmap, outer keys represent toll plaza locations, inner keys represent lane numbers at
     respective toll plazas, and values represent time-expanded tolling instances
     */

     private static final long SECONDS_IN_YEAR = 31_536_000;
     private static final long SECONDS_IN_MONTH = 2_628_000;
     private static final long SECONDS_IN_DAY = 86_400;
     private static final long SECONDS_IN_HOUR = 3_600;
     private static final long SECONDS_IN_MINUTE = 60;

     /**
      * Behaviour definitions
      */
     public LinkedHashMap<String, LinkedHashMap<Integer, LinkedHashMap<Long, VehicleTollingInstance>>> readTollData
     (String tollDataFilePath) {
          try {
               // Set up reader instance
               BufferedReader tollDataReader = new BufferedReader(new FileReader(tollDataFilePath));
               String newline;

               // Read header array
               String[] tollDataHeaderArray = tollDataReader.readLine().split(",");
               // System.out.println(Arrays.toString(tollDataHeaderArray));     // Debug: Check header array

               int vehicleRegistrationIndex = findIndexInArray("VehRegNo", tollDataHeaderArray);
               int vehicleFASTagIdIndex = findIndexInArray("TagID", tollDataHeaderArray);
               int vehicleWeightInMotionIndex = findIndexInArray("WIMweight", tollDataHeaderArray);
               int vehicleWeightStaticIndex = findIndexInArray("StaticWimWeight", tollDataHeaderArray);
               int permissibleVehicleWeightStaticIndex = findIndexInArray("PermisibleWeight", tollDataHeaderArray);
               int autoIdentifiedVehicleClassIndex = findIndexInArray("AVCClass", tollDataHeaderArray);
               int employeeSpecifiedVehicleClassIndex = findIndexInArray("SVCClass", tollDataHeaderArray);

               int transactionIdIndex = findIndexInArray("TransactionID", tollDataHeaderArray);
               int bankTransactionIdIndex = findIndexInArray("BankTransactionId", tollDataHeaderArray);
               int transactionDateIndex = findIndexInArray("Date", tollDataHeaderArray);
               int transactionTimeIndex = findIndexInArray("Time", tollDataHeaderArray);
               int roadUsageFareIndex = findIndexInArray("SVCFare", tollDataHeaderArray);
               int overloadingFareIndex = findIndexInArray("OverloadedFare", tollDataHeaderArray);
               int penaltyFareIndex = findIndexInArray("PenaltyFare", tollDataHeaderArray);
               int paymentModeIndex = findIndexInArray("PaymentMethod", tollDataHeaderArray);
               int transactionRemarkIndex = findIndexInArray("TransactionDescription", tollDataHeaderArray);
               int journeyTypeIndex = findIndexInArray("JourneyType", tollDataHeaderArray);

               int employeeIdIndex = findIndexInArray("UserId", tollDataHeaderArray);
               int laneNumberIndex = findIndexInArray("LaneNo", tollDataHeaderArray);
               int shiftOrderIndex = findIndexInArray("Shift", tollDataHeaderArray);
               int locationNameIndex = findIndexInArray("SourceTable", tollDataHeaderArray);

               // Read data and populate location- and lane-wise lists of tolling instances
               while((newline = tollDataReader.readLine()) != null) {
                    String[] tollingDataRecord = newline.split(",");
                    // Handle cases wherein multi-line entries exist within the dataframe
                    if (tollingDataRecord.length <= 29) {
                         continue;
                    }

                    // System.out.println(Arrays.toString(tollingDataRecord));  // Debug: Check data record
                    String vehicleRegistration = tollingDataRecord[vehicleRegistrationIndex];
                    String vehicleFASTagId = tollingDataRecord[vehicleFASTagIdIndex];
                    int vehicleWeightInMotion = Integer.parseInt(tollingDataRecord[vehicleWeightInMotionIndex]);
                    int vehicleWeightStatic = Integer.parseInt(tollingDataRecord[vehicleWeightStaticIndex]);
                    int permissibleVehicleWeightStatic = tollingDataRecord[permissibleVehicleWeightStaticIndex].
                            isEmpty() ? 0 : Integer.parseInt(tollingDataRecord[permissibleVehicleWeightStaticIndex]);
                    String autoIdentifiedVehicleClass = tollingDataRecord[autoIdentifiedVehicleClassIndex];
                    String employeeSpecifiedVehicleClass = tollingDataRecord[employeeSpecifiedVehicleClassIndex];

                    long transactionId = Long.parseLong(tollingDataRecord[transactionIdIndex].replaceAll("\"",
                            ""));
                    String bankTransactionId = tollingDataRecord[bankTransactionIdIndex];
                    String transactionDate = tollingDataRecord[transactionDateIndex];
                    String transactionTime = tollingDataRecord[transactionTimeIndex];
                    long transactionTimeSeconds = Long.parseLong(transactionDate.substring(0, 4)) * SECONDS_IN_YEAR +
                            Long.parseLong(transactionDate.substring(5, 7)) * SECONDS_IN_MONTH +
                            Long.parseLong(transactionDate.substring(8)) * SECONDS_IN_DAY +
                            Long.parseLong(transactionTime.substring(0, 2)) * SECONDS_IN_HOUR +
                            Long.parseLong(transactionTime.substring(3, 5)) * SECONDS_IN_MINUTE +
                            Long.parseLong(transactionTime.substring(6, 8));
                    /* Debugging statements:
                    System.out.println("\n" +
                            "Transaction date: " + transactionDate + "\n" +
                            "Transaction time: " + transactionTime + "\n" +
                            "Transaction time seconds: " + transactionTimeSeconds + "\n");
                    */

                    int roadUsageFare = Integer.parseInt(tollingDataRecord[roadUsageFareIndex]);
                    int overloadingFare = Integer.parseInt(tollingDataRecord[overloadingFareIndex]);
                    int penaltyFare = Integer.parseInt(tollingDataRecord[penaltyFareIndex]);
                    int totalFare = roadUsageFare + overloadingFare + penaltyFare;
                    String paymentMode = tollingDataRecord[paymentModeIndex];
                    String transactionRemark = tollingDataRecord[transactionRemarkIndex];
                    String journeyType = tollingDataRecord[journeyTypeIndex];

                    String employeeId = tollingDataRecord[employeeIdIndex];
                    int laneNumber = Integer.parseInt(tollingDataRecord[laneNumberIndex].substring(1));
                    String shiftOrder = tollingDataRecord[shiftOrderIndex].substring(6);
                    String locationName = tollingDataRecord[locationNameIndex];

                    VehicleTollingInstance vehicleTollingInstance = new VehicleTollingInstance(vehicleRegistration,
                            vehicleFASTagId, vehicleWeightInMotion, vehicleWeightStatic, permissibleVehicleWeightStatic,
                            autoIdentifiedVehicleClass, employeeSpecifiedVehicleClass, transactionId, bankTransactionId,
                            transactionTimeSeconds, roadUsageFare, overloadingFare, penaltyFare, totalFare, paymentMode,
                            transactionRemark, journeyType, employeeId, laneNumber, shiftOrder, locationName);

                    // Structuring the plaza- and lane-wise maps
                    if(!plazaLaneWiseTollingInstanceMaps.containsKey(locationName)) {
                         plazaLaneWiseTollingInstanceMaps.put(locationName, new LinkedHashMap<>());
                    }

                    if(!plazaLaneWiseTollingInstanceMaps.get(locationName).containsKey(laneNumber)) {
                         plazaLaneWiseTollingInstanceMaps.get(locationName).put(laneNumber, new LinkedHashMap<>());
                    }

                    plazaLaneWiseTollingInstanceMaps.get(locationName).get(laneNumber).put(transactionTimeSeconds,
                            vehicleTollingInstance);
               }

          } catch (FileNotFoundException fNFE) {
               System.out.println("File not found at " + tollDataFilePath);
          } catch (IOException iOE) {
               System.out.println("Input-output exception; please review the file at " + tollDataFilePath);
          }

          return plazaLaneWiseTollingInstanceMaps;
     }

     public void writeTollData(String tollDataOutputFilePath) {}

     private int findIndexInArray(String columnHeaderName, String[] columnHeaderArray) {
          int columnIndex = -1;
          for (int i = 0; i <= columnHeaderArray.length; i += 1) {
               if (columnHeaderArray[i].equalsIgnoreCase(columnHeaderName)) {
                    columnIndex = i;
                    break;
               }
          }

          return columnIndex;
     }
}