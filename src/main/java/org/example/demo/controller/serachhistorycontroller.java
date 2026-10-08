package org.example.demo.controller;

import org.example.demo.Entity.SearchHistory;
import org.example.demo.Entity.Tourist;
import org.example.demo.Service.searchhistoryservice;
import org.example.demo.Service.touristservice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/history")
class SearchHistoryController {

    private final searchhistoryservice searchHistoryService;
    private final touristservice touristService;

    public SearchHistoryController(
            searchhistoryservice searchHistoryService,
            touristservice touristService
    ) {
        this.searchHistoryService = searchHistoryService;
        this.touristService = touristService;
    }

    // Save a search
    @PostMapping
    public ResponseEntity<SearchHistory> saveSearch(
            @RequestBody SearchHistoryRequest request,
            Authentication authentication
    ) {

        String email = authentication.getName();

        Tourist tourist = touristService.getTouristByEmail(email);

        SearchHistory savedSearch =
                searchHistoryService.saveSearch(
                        tourist,
                        request.getLatitude(),
                        request.getLongitude(),
                        request.getSafetyScore(),
                        request.getCategory(),
                        request.getTopRiskFactor()
                );

        return ResponseEntity.ok(savedSearch);
    }

    // Get recent 5 searches
    @GetMapping("/recent")
    public ResponseEntity<List<SearchHistory>> getRecentSearches(
            Authentication authentication
    ) {

        String email = authentication.getName();

        Tourist tourist = touristService.getTouristByEmail(email);

        return ResponseEntity.ok(
                searchHistoryService.getRecentSearches(tourist)
        );
    }

    // Get all searches
    @GetMapping
    public ResponseEntity<List<SearchHistory>> getAllSearches(
            Authentication authentication
    ) {

        String email = authentication.getName();

        Tourist tourist = touristService.getTouristByEmail(email);

        return ResponseEntity.ok(
                searchHistoryService.getAllSearches(tourist)
        );
    }

    // Get total number of searches
    @GetMapping("/count")
    public ResponseEntity<Long> getTotalSearches(
            Authentication authentication
    ) {

        String email = authentication.getName();

        Tourist tourist = touristService.getTouristByEmail(email);

        return ResponseEntity.ok(
                searchHistoryService.getTotalSearches(tourist)
        );
    }

    // Delete search history
    @DeleteMapping
    public ResponseEntity<String> deleteSearchHistory(
            Authentication authentication
    ) {

        String email = authentication.getName();

        Tourist tourist = touristService.getTouristByEmail(email);

        searchHistoryService.deleteSearchHistory(tourist);

        return ResponseEntity.ok("Search history deleted successfully");
    }


    // Request DTO
    public static class SearchHistoryRequest {

        private Double latitude;
        private Double longitude;
        private Double safetyScore;
        private String category;
        private String topRiskFactor;

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Double getSafetyScore() {
            return safetyScore;
        }

        public void setSafetyScore(Double safetyScore) {
            this.safetyScore = safetyScore;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getTopRiskFactor() {
            return topRiskFactor;
        }

        public void setTopRiskFactor(String topRiskFactor) {
            this.topRiskFactor = topRiskFactor;
        }
    }
}