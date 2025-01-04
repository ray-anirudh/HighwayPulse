HighwayPulse README

1. What It Does

The HighwayPulse is a Java-based data processing pipeline developed to analyze tolling microdata, geospatial data, and vehicle metrics. It generates detailed insights into traffic state estimation, charging infrastructure planning, and EV simulations for highway sections. Key use cases include:

Traffic state estimation: Understand vehicle behavior on highways over time.

EV infrastructure planning: Identify optimal locations for electric vehicle charging stations (EVCI).

Energy demand simulations: Estimate energy requirements and charging probabilities for vehicles.

HighwayPulse is versatile and applicable to any road stretch in India with similar inputs.

2. Salient Features

High granularity: Provides second-by-second analysis of vehicle movements and charging behaviors.

Agent-based simulation: Models individual vehicles, accounting for energy usage, halting behavior, and charging probabilities.

Infrastructure planning: Generates demand heatmaps and suggests EVCI placement.

Extensible outputs: Compatible with tools like QGIS, GRASS, ArcGIS, and Power BI for spatial visualization.

Transferability: Easily adapted for other highway sections with diverse datasets.

3. Inputs to the Program

HighwayPulse requires several datasets in machine-readable formats (CSV, XML, TXT, etc.):

a. Tolling Microdata

Purpose: Tracks vehicle tolling events, including registration numbers, classes, and timestamps.

Example fields:

VehicleRegistration

EmployeeSpecifiedVehicleClass

TransactionTimeSeconds

b. Date-Time Stamps

Purpose: Defines specific moments for instance generation.

Format: CSV file with Unix timestamps.

c. Geospatial Data

Road Stretch Coordinates: Maps distances along the highway to geographic coordinates.

Example file: DelAgrRoadStretchAsPoints1m-7760Dissolved.csv

Points of Interest (POIs): Defines halting points with associated data.

Example file: PointsOfInterestNH44.csv

4. Outputs of the Program

JTDA produces two primary outputs:

a. Mobile Vehicle Instances

Description: Provides detailed information on vehicles on the highway, including their location, energy state, and halting behavior.

Format: CSV files with fields like:

VehicleRegistration

AscribedXCoordinate

BatteryCapacityKWh

ChargingProbability

b. Halting Point Instances

Description: Aggregates metrics for halting points, such as energy demand and average vehicle halting duration.

Format: CSV files with fields like:

AggregateChargingDemandKWh

AverageChargingProbability

PassengerVehicleCount

Both outputs are organized by timestamp, enabling temporal analysis.

5. How to Run the Program

Prerequisites

Java SE Development Kit (JDK): Download and install the latest version from the Oracle JDK Downloads.

Integrated Development Environment (IDE): Install IntelliJ IDEA or any other Java IDE.

Steps to Run

Clone or Download the Project

Ensure all .java files and input data are in the project directory.

Set Up the Environment

Open IntelliJ IDEA.

Create a new project and select the downloaded directory as the project folder.

Configure the JDK:

Go to File > Project Structure > SDKs.

Add the installed JDK path.

Modify Input Paths

Update file paths in the code (e.g., tollingMicroDataFilePath) to point to the correct input files on your system.

Build and Run

Build the project to resolve dependencies.

Run the main method in HighwayInstancesBuilder.java.

View Outputs

Navigate to the specified output directories for CSV files.

Analyze the data using GIS tools or data analysis platforms.

Notes

Ensure sufficient memory allocation for the JVM as large datasets might cause out-of-memory errors.

Outputs can be aggregated externally for visualization and further analysis.

Adjust parameters like battery capacity or halting behavior assumptions to simulate different scenarios.
