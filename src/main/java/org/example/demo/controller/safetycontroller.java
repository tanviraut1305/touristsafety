
package org.example.demo.controller;

import org.example.demo.Service.safetyservice;
import org.example.demo.Service.CrimeSpatialService;
import org.example.demo.dto.SafetyRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/safety")
public class safetycontroller {

    private final safetyservice safetyService;
    private final CrimeSpatialService crimeSpatialService;

    public safetycontroller(
            safetyservice safetyService,
            CrimeSpatialService crimeSpatialService) {
        this.safetyService = safetyService;
        this.crimeSpatialService = crimeSpatialService;
    }

    @PostMapping("/predict")
    public Map<String, Object> predictSafety(
            @RequestBody SafetyRequest request) {

        double safetyScore = safetyService.checkSafety(request);

        String safetyCategory =
                safetyService.getSafetyCategory(safetyScore);

        Map<String, Double> crimeFeatures =
                crimeSpatialService.calculateSpatialFeatures(
                        request.getLatitude(),
                        request.getLongitude()
                );

        Map<String, Object> response = new HashMap<>();

        response.put("latitude", request.getLatitude());
        response.put("longitude", request.getLongitude());
        response.put("safety_score", safetyScore);
        response.put("safety_category", safetyCategory);
        response.put(
                "crime_count_500m",
                crimeFeatures.get("crime_count_500m")
        );
        response.put(
                "crime_severity_500m",
                crimeFeatures.get("crime_severity_500m")
        );
        response.put(
                "crime_density_500m",
                crimeFeatures.get("crime_density_500m")
        );
        response.put(
                "nearby_record_count",
                crimeFeatures.get("nearby_record_count")
        );

        return response;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleInvalidInput(
            IllegalArgumentException ex) {

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());

        return ResponseEntity.badRequest().body(errorResponse);
    }
}