package org.example.demo.Repository;

import org.example.demo.Entity.SearchHistory;
import org.example.demo.Entity.Tourist;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface searchhistory
        extends JpaRepository<SearchHistory, Long> {

    // Get recent searches for a tourist
    List<SearchHistory> findByTouristOrderBySearchedAtDesc(
            Tourist tourist,
            Pageable pageable
    );

    // Count total searches made by a tourist
    long countByTourist(Tourist tourist);

    // Get all searches of a tourist
    List<SearchHistory> findByTouristOrderBySearchedAtDesc(
            Tourist tourist
    );

    // Delete all search history of a tourist
    void deleteByTourist(Tourist tourist);
}