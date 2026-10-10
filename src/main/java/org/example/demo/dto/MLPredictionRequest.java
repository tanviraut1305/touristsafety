package org.example.demo.dto;

public class MLPredictionRequest {

    private double crime_count_500m;
    private double crime_severity_500m;
    private double crime_density_500m;
    private double nearby_record_count;

    public MLPredictionRequest() {
    }

    public MLPredictionRequest(
            double crime_count_500m,
            double crime_severity_500m,
            double crime_density_500m,
            double nearby_record_count) {
        this.crime_count_500m = crime_count_500m;
        this.crime_severity_500m = crime_severity_500m;
        this.crime_density_500m = crime_density_500m;
        this.nearby_record_count = nearby_record_count;
    }

    public double getCrime_count_500m() {
        return crime_count_500m;
    }

    public double getCrime_severity_500m() {
        return crime_severity_500m;
    }

    public double getCrime_density_500m() {
        return crime_density_500m;
    }

    public double getNearby_record_count() {
        return nearby_record_count;
    }


}
