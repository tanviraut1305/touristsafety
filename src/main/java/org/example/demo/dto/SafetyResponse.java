package org.example.demo.dto;

public class SafetyResponse {

    private Double safetyScore;
    private String category;
    private String color;
    private Double womenSafetyScore;
    private String womenSafetyCategory;
    private String topRiskFactor;
    private String reason;

    public SafetyResponse() {
    }

    public SafetyResponse(
            Double safetyScore,
            String category,
            String color,
            Double womenSafetyScore,
            String womenSafetyCategory,
            String topRiskFactor,
            String reason
    ) {
        this.safetyScore = safetyScore;
        this.category = category;
        this.color = color;
        this.womenSafetyScore = womenSafetyScore;
        this.womenSafetyCategory = womenSafetyCategory;
        this.topRiskFactor = topRiskFactor;
        this.reason = reason;
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

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Double getWomenSafetyScore() {
        return womenSafetyScore;
    }

    public void setWomenSafetyScore(Double womenSafetyScore) {
        this.womenSafetyScore = womenSafetyScore;
    }

    public String getWomenSafetyCategory() {
        return womenSafetyCategory;
    }

    public void setWomenSafetyCategory(String womenSafetyCategory) {
        this.womenSafetyCategory = womenSafetyCategory;
    }

    public String getTopRiskFactor() {
        return topRiskFactor;
    }

    public void setTopRiskFactor(String topRiskFactor) {
        this.topRiskFactor = topRiskFactor;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}