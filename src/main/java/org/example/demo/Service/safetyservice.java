
package org.example.demo.Service;

import org.example.demo.dto.MLPredictionRequest;
import org.example.demo.dto.SafetyRequest;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class safetyservice {

    private final CrimeSpatialService crimeSpatialService;
    private final MLPredictionService mlPredictionService;

    public safetyservice(
            CrimeSpatialService crimeSpatialService,
            MLPredictionService mlPredictionService) {
        this.crimeSpatialService = crimeSpatialService;
        this.mlPredictionService = mlPredictionService;
    }

    public double checkSafety(SafetyRequest request) {

        if (request.getLatitude() == null
                || request.getLatitude() < -90
                || request.getLatitude() > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (request.getLongitude() == null
                || request.getLongitude() < -180
                || request.getLongitude() > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        Map<String, Double> features =
                crimeSpatialService.calculateSpatialFeatures(
                        request.getLatitude(),
                        request.getLongitude()
                );

        MLPredictionRequest mlRequest =
                new MLPredictionRequest(
                        features.get("crime_count_500m"),
                        features.get("crime_severity_500m"),
                        features.get("crime_density_500m"),
                        features.get("nearby_record_count")
                );

        return mlPredictionService.predictSafety(mlRequest);
    }

    public String getSafetyCategory(double safetyScore) {
        if (safetyScore >= 70) {
            return "Safe";
        } else if (safetyScore >= 40) {
            return "Moderate Risk";
        } else {
            return "High Risk";
        }
    }
}