package org.example.demo.dto;

public class SafetyRequest {

    private Double latitude;
    private Double longitude;

    public SafetyRequest() {
    }

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
    @Override
    public String toString() {
        return "SafetyRequest{" +"latitude=" + latitude +", longitude=" + longitude +'}';
    }

}