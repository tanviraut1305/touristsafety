package org.example.demo.dto;

public class MLPredictionResponse {

    private double safety_score;

    public MLPredictionResponse() {
    }

    public MLPredictionResponse(double safety_score) {
        this.safety_score = safety_score;
    }

    public double getSafety_score() {
        return safety_score;
    }

    public void setSafety_score(double safety_score) {
        this.safety_score = safety_score;
    }

}
