package Utils;

import java.io.*;
import java.util.ArrayList;

public class DateTimeListReader {
    // Functionality on point - 16.12.2024

    public ArrayList<Long> secondsForVisualizingVehicleLocations = new ArrayList<>();
    // Stores temporal values in seconds as the time stamps for which highway instances are to be created

    private static final long SECONDS_IN_YEAR = 31_536_000;
    private static final long SECONDS_IN_MONTH = 2_628_000;
    private static final long SECONDS_IN_DAY = 86_400;
    private static final long SECONDS_IN_HOUR = 3_600;
    private static final long SECONDS_IN_MINUTE = 60;

    public ArrayList<Long> readDateTimeList(String dateTimeListFilePath) {
        try {
            BufferedReader dateTimeListReader = new BufferedReader(new FileReader(dateTimeListFilePath));
            String newline;

            // Read header array of the date-time list
            String[] dateTimeListHeader = dateTimeListReader.readLine().split(",");

            int dateIndex = findIndexInArray("Date", dateTimeListHeader);
            int timeIndex = findIndexInArray("Time", dateTimeListHeader);

            // Read body and process data
            while ((newline = dateTimeListReader.readLine()) != null) {
                String[] dateTimeRecord = newline.split(",");

                // System.out.println(Arrays.toString(dateTimeRecord));   // Debug: Check date-time record
                String date = dateTimeRecord[dateIndex];
                String time = dateTimeRecord[timeIndex];

                long secondsValueOfDateTimeRecord = Long.parseLong(date.substring(0, 2)) * SECONDS_IN_DAY +
                        Long.parseLong(date.substring(3, 5)) * SECONDS_IN_MONTH +
                        Long.parseLong(date.substring(6, 10)) * SECONDS_IN_YEAR +
                        Long.parseLong(time.substring(0, 2)) * SECONDS_IN_HOUR +
                        Long.parseLong(time.substring(3, 5)) * SECONDS_IN_MINUTE +
                        Long.parseLong(time.substring(6, 8));

                this.secondsForVisualizingVehicleLocations.add(secondsValueOfDateTimeRecord);
                // System.out.println(secondsValueOfDateTimeRecord);   // Debug: Check the seconds values
            }

        } catch (FileNotFoundException fNFE) {
            System.out.println("File not found at " + dateTimeListFilePath);
        } catch (IOException iOE) {
            System.out.println("Input-output exception; please review file at: " + dateTimeListFilePath);
        }

        return this.secondsForVisualizingVehicleLocations;
    }

    private int findIndexInArray(String columnHeaderName, String[] columnHeaderArray) {
        int columnIndex = -1;
        for (int i = 0; i < columnHeaderArray.length; i++) {
            // Address Byte Order Mark characters (unprintable), if present
            columnHeaderArray[i] = columnHeaderArray[i].replaceAll("[^\\p{Print}]", "");

            if (columnHeaderArray[i].equalsIgnoreCase(columnHeaderName)) {
                columnIndex = i;
                break;
            }
        }

        return columnIndex;
    }
}