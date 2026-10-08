package org.example.demo.Service;

import org.example.demo.Entity.SearchHistory;
import org.example.demo.Entity.Tourist;
import org.example.demo.Repository.searchhistory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class searchhistoryservice{

    private final searchhistory searchHistoryRepository;

    public searchhistoryservice(searchhistory searchHistoryRepository) {
        this.searchHistoryRepository = searchHistoryRepository;
    }

    // Save a new search
    public SearchHistory saveSearch(
            Tourist tourist,
            Double latitude,
            Double longitude,
            Double safetyScore,
            String category,
            String topRiskFactor
    ) {

        SearchHistory searchHistory = new SearchHistory();

        searchHistory.setTourist(tourist);
        searchHistory.setLatitude(latitude);
        searchHistory.setLongitude(longitude);
        searchHistory.setSafetyScore(safetyScore);
        searchHistory.setCategory(category);
        searchHistory.setTopRiskFactor(topRiskFactor);
        searchHistory.setSearchedAt(LocalDateTime.now());

        return searchHistoryRepository.save(searchHistory);
    }

    // Get latest 5 searches
    public List<SearchHistory> getRecentSearches(Tourist tourist) {

        return searchHistoryRepository
                .findByTouristOrderBySearchedAtDesc(
                        tourist,
                        PageRequest.of(0, 5)
                );
    }

    // Get all searches
    public List<SearchHistory> getAllSearches(Tourist tourist) {

        return searchHistoryRepository
                .findByTouristOrderBySearchedAtDesc(tourist);
    }

    // Count total searches
    public long getTotalSearches(Tourist tourist) {

        return searchHistoryRepository.countByTourist(tourist);
    }

    // Delete search history
    public void deleteSearchHistory(Tourist tourist) {

        searchHistoryRepository.deleteByTourist(tourist);
    }
}