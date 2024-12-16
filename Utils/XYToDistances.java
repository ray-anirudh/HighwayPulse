package Utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;

public class XYToDistances {
    // Functionality on point - 16.12.2024

    /**
     *  Attribute definitions
     */
    private static final int DELHI_AGRA_DISTANCE_M = 167_215;
    private final LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromDel = new LinkedHashMap<>();
    // Maps cumulative distances to coordinates for the Delhi-Agra direction

    private final LinkedHashMap<Integer, CoordinatesDistances> distanceVsCoordinatesMapFromAgr = new LinkedHashMap<>();
    // Maps cumulative distances to coordinates for the Agra-Delhi direction

    /**
     * Behaviour definitions
     */
    public void readCoordinatesAndTranslateToDistances (String XYDataFilePath) {
        try {
            // Set up reader instance
            BufferedReader XYDataReader = new BufferedReader(new FileReader(XYDataFilePath));
            String newline;

            // Read header array
            String[] XYDataHeaderArray = XYDataReader.readLine().split(",");
            // System.out.println(Arrays.toString(XYDataHeaderArray));     // Debug: Check header array

            int xCoordinateIndex = findIndexInArray("xCoordinate", XYDataHeaderArray);
            int yCoordinateIndex = findIndexInArray("yCoordinate", XYDataHeaderArray);

            // Read data and populate location- and lane-wise lists of tolling instances
            double precedingXCoordinate = 0;
            double precedingYCoordinate = 0;
            double cumulativeDistanceFromDelhi = 0;

            while((newline = XYDataReader.readLine()) != null) {
                String[] XYDataRecord = newline.split(",");
                double xCoordinate = Double.parseDouble(XYDataRecord[xCoordinateIndex]);
                double yCoordinate = Double.parseDouble(XYDataRecord[yCoordinateIndex]);

                if (precedingYCoordinate != 0) {
                    double xDistance = xCoordinate - precedingXCoordinate;
                    double yDistance = yCoordinate - precedingYCoordinate;
                    cumulativeDistanceFromDelhi += Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
                }

                CoordinatesDistances coordinatesDistances = new CoordinatesDistances(xCoordinate, yCoordinate,
                        cumulativeDistanceFromDelhi, (DELHI_AGRA_DISTANCE_M - cumulativeDistanceFromDelhi));

                /* Debug: Check data records
                System.out.println("X-coordinate: " + xCoordinate + "\n" +
                         "Y-coordinate: " + yCoordinate + "\n" +
                         "Distance from Delhi: " + cumulativeDistanceFromDelhi + "\n" +
                         "Distance from Agra: " + (DELHI_AGRA_DISTANCE_M - cumulativeDistanceFromDelhi) + "\n");
                */

                this.distanceVsCoordinatesMapFromDel.put((int) cumulativeDistanceFromDelhi, coordinatesDistances);
                this.distanceVsCoordinatesMapFromAgr.put((int) (DELHI_AGRA_DISTANCE_M - cumulativeDistanceFromDelhi),
                        coordinatesDistances);

                precedingXCoordinate = xCoordinate;
                precedingYCoordinate = yCoordinate;
            }

        } catch (FileNotFoundException fNFE) {
            System.out.println("File not found at " + XYDataFilePath);
        } catch (IOException iOE) {
            System.out.println("Input-output exception; please review the file at " + XYDataFilePath);
        }
    }

    public LinkedHashMap<Integer, CoordinatesDistances> getDistanceVsCoordinatesMapFromDel() {
        return this.distanceVsCoordinatesMapFromDel;
    }

    public LinkedHashMap<Integer, CoordinatesDistances> getDistanceVsCoordinatesMapFromAgr() {
        return this.distanceVsCoordinatesMapFromAgr;
    }

    private int findIndexInArray(String columnHeaderName, String[] columnHeaderArray) {
        int columnIndex = -1;
        for (int i = 0; i < columnHeaderArray.length; i += 1) {
            if (columnHeaderArray[i].equalsIgnoreCase(columnHeaderName)) {
                columnIndex = i;
                break;
            }
        }

        return columnIndex;
    }
}