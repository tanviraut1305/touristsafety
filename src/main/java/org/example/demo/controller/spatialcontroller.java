package org.example.demo.controller;

import org.example.demo.dto.SafetyRequest;
import org.example.demo.dto.spatialfeature;
import org.example.demo.Service.CrimeSpatialService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/spatial")
public class spatialcontroller {

    private final CrimeSpatialService crimeSpatialService;

    public spatialcontroller(
            CrimeSpatialService crimeSpatialService) {

        this.crimeSpatialService = crimeSpatialService;
    }

    @PostMapping("/features")
    public ResponseEntity<spatialfeature> calculateFeatures(
            @RequestBody SafetyRequest request) {

        Map<String, Double> features =
                crimeSpatialService.calculateSpatialFeatures(
                        request.getLatitude(),
                        request.getLongitude()
                );

        spatialfeature response =
                new spatialfeature(
                        features.get("crime_count_500m"),
                        features.get("crime_severity_500m"),
                        features.get("crime_density_500m"),
                        features.get("nearby_record_count")
                );

        return ResponseEntity.ok(response);
    }
}