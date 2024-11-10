package Utils;

public class CoordinatesDistances {
    private double xCoordinate;
    private double yCoordinate;
    private double distanceFromDelhi;
    private double distanceFromAgra;

    CoordinatesDistances(double xCoordinate, double yCoordinate, double distanceFromDelhi, double distanceFromAgra) {
        this.xCoordinate = xCoordinate;
        this.yCoordinate = yCoordinate;
        this.distanceFromDelhi = distanceFromDelhi;
        this.distanceFromAgra = distanceFromAgra;
    }

    public double getXCoordinate() {
        return this.xCoordinate;
    }

    public double getYCoordinate() {
        return this.yCoordinate;
    }
}
