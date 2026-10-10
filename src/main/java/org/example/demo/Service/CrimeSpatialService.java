package org.example.demo.Service;

import org.example.demo.Entity.crimerecord;
import org.example.demo.Repository.CrimeRecordRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CrimeSpatialService {

    private final CrimeRecordRepository crimeRecordRepository;

    // 500 meters
    private static final double RADIUS_KM = 0.5;

    // Area of a circle with radius 0.5 km
    private static final double AREA_KM2 =
            Math.PI * RADIUS_KM * RADIUS_KM;

    public CrimeSpatialService(
            CrimeRecordRepository crimeRecordRepository) {

        this.crimeRecordRepository = crimeRecordRepository;
    }

    public Map<String, Double> calculateSpatialFeatures(
            Double latitude,
            Double longitude) {

        if (latitude == null || latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Invalid latitude");
        }

        if (longitude == null || longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Invalid longitude");
        }

        List<crimerecord> records =
                crimeRecordRepository.findAll();

        int nearbyRecordCount = 0;

        double crimeCount500m = 0.0;
        double crimeSeverity500m = 0.0;

        for (crimerecord record : records) {

            if (record.getLatitude() == null ||
                    record.getLongitude() == null) {
                continue;
            }

            double distance = calculateDistanceKm(
                    latitude,
                    longitude,
                    record.getLatitude(),
                    record.getLongitude()
            );

            // Check whether the record is within 500 meters
            if (distance <= RADIUS_KM) {

                nearbyRecordCount++;

                // Total crime incidents in this record
                double recordCrimeCount =
                        calculateTotalCrime(record);

                crimeCount500m += recordCrimeCount;

                // Weighted crime severity
                crimeSeverity500m +=
                        calculateCrimeSeverity(record);
            }
        }

        // Crime density = crime count / area
        double crimeDensity500m =
                crimeCount500m / AREA_KM2;

        Map<String, Double> result =
                new HashMap<>();

        result.put(
                "crime_count_500m",
                crimeCount500m
        );

        result.put(
                "crime_severity_500m",
                crimeSeverity500m
        );

        result.put(
                "crime_density_500m",
                crimeDensity500m
        );

        result.put(
                "nearby_record_count",
                (double) nearbyRecordCount
        );

        return result;
    }

    /**
     * Calculates the total number of crime incidents
     * contained in one CrimeRecord.
     */
    private double calculateTotalCrime(
            crimerecord record) {

        return safe(record.getMurder())
                + safe(record.getAttemptToMurder())
                + safe(record.getRobbery())
                + safe(record.getDacoity())
                + safe(record.getTheft())
                + safe(record.getHouseBreaking())
                + safe(record.getVehicleTheft())
                + safe(record.getAssault())
                + safe(record.getCrimesAgainstWomen())
                + safe(record.getCyberCrime())
                + safe(record.getKidnapping())
                + safe(record.getRape())
                + safe(record.getMolestation())
                + safe(record.getChainSnatching())
                + safe(record.getPickpocketing())
                + safe(record.getMissingPerson())
                + safe(record.getCheating())
                + safe(record.getDrugRelatedCrime());
    }

    /**
     * Calculates weighted crime severity.
     *
     * These weights match the configuration used
     * by the trained safety model.
     */
    private double calculateCrimeSeverity(
            crimerecord record) {

        double severity = 0.0;

        severity += safe(record.getMurder()) * 10;
        severity += safe(record.getAttemptToMurder()) * 9;
        severity += safe(record.getRobbery()) * 7;
        severity += safe(record.getDacoity()) * 8;
        severity += safe(record.getTheft()) * 3;
        severity += safe(record.getHouseBreaking()) * 4;
        severity += safe(record.getVehicleTheft()) * 3;
        severity += safe(record.getAssault()) * 6;
        severity += safe(record.getCrimesAgainstWomen()) * 7;
        severity += safe(record.getCyberCrime()) * 2;
        severity += safe(record.getKidnapping()) * 8;
        severity += safe(record.getRape()) * 10;
        severity += safe(record.getMolestation()) * 7;
        severity += safe(record.getChainSnatching()) * 5;
        severity += safe(record.getPickpocketing()) * 3;
        severity += safe(record.getMissingPerson()) * 2;
        severity += safe(record.getCheating()) * 2;
        severity += safe(record.getDrugRelatedCrime()) * 4;

        return severity;
    }

    /**
     * Haversine formula.
     *
     * Returns distance between two latitude/longitude
     * coordinates in kilometers.
     */
    private double calculateDistanceKm(
            double lat1,
            double lon1,
            double lat2,
            double lon2) {

        final double EARTH_RADIUS_KM = 6371.0;

        double lat1Rad =
                Math.toRadians(lat1);

        double lat2Rad =
                Math.toRadians(lat2);

        double deltaLat =
                Math.toRadians(lat2 - lat1);

        double deltaLon =
                Math.toRadians(lon2 - lon1);

        double a =
                Math.sin(deltaLat / 2)
                        * Math.sin(deltaLat / 2)
                        +
                        Math.cos(lat1Rad)
                                * Math.cos(lat2Rad)
                                * Math.sin(deltaLon / 2)
                                * Math.sin(deltaLon / 2);

        double c =
                2 * Math.atan2(
                        Math.sqrt(a),
                        Math.sqrt(1 - a)
                );

        return EARTH_RADIUS_KM * c;
    }

    /**
     * Prevents NullPointerException when a crime
     * value is null.
     */
    private int safe(Integer value) {
        return value == null ? 0 : value;
    }
}