package org.example.demo.dto;

public class spatialfeature{

    private Double crimeCount500m;
    private Double crimeSeverity500m;
    private Double crimeDensity500m;
    private Double nearbyRecordCount;

    public spatialfeature() {
    }

    public spatialfeature(
            Double crimeCount500m,
            Double crimeSeverity500m,
            Double crimeDensity500m,
            Double nearbyRecordCount) {

        this.crimeCount500m = crimeCount500m;
        this.crimeSeverity500m = crimeSeverity500m;
        this.crimeDensity500m = crimeDensity500m;
        this.nearbyRecordCount = nearbyRecordCount;
    }

    public Double getCrimeCount500m() {
        return crimeCount500m;
    }

    public void setCrimeCount500m(Double crimeCount500m) {
        this.crimeCount500m = crimeCount500m;
    }

    public Double getCrimeSeverity500m() {
        return crimeSeverity500m;
    }

    public void setCrimeSeverity500m(Double crimeSeverity500m) {
        this.crimeSeverity500m = crimeSeverity500m;
    }

    public Double getCrimeDensity500m() {
        return crimeDensity500m;
    }

    public void setCrimeDensity500m(Double crimeDensity500m) {
        this.crimeDensity500m = crimeDensity500m;
    }

    public Double getNearbyRecordCount() {
        return nearbyRecordCount;
    }

    public void setNearbyRecordCount(Double nearbyRecordCount) {
        this.nearbyRecordCount = nearbyRecordCount;
    }
}