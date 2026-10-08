package org.example.demo.Entity;

import jakarta.persistence.*;

@Entity
@Table(name = "crime_records")
public class crimerecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double latitude;
    private Double longitude;

    private Integer murder;
    private Integer attemptToMurder;
    private Integer robbery;
    private Integer dacoity;
    private Integer theft;
    private Integer houseBreaking;
    private Integer vehicleTheft;
    private Integer assault;
    private Integer crimesAgainstWomen;
    private Integer cyberCrime;
    private Integer kidnapping;
    private Integer rape;
    private Integer molestation;
    private Integer chainSnatching;
    private Integer pickpocketing;
    private Integer missingPerson;
    private Integer cheating;
    private Integer drugRelatedCrime;

    public crimerecord() {
    }

    public Long getId() {
        return id;
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

    public Integer getMurder() {
        return murder;
    }

    public void setMurder(Integer murder) {
        this.murder = murder;
    }

    public Integer getAttemptToMurder() {
        return attemptToMurder;
    }

    public void setAttemptToMurder(Integer attemptToMurder) {
        this.attemptToMurder = attemptToMurder;
    }

    public Integer getRobbery() {
        return robbery;
    }

    public void setRobbery(Integer robbery) {
        this.robbery = robbery;
    }

    public Integer getDacoity() {
        return dacoity;
    }

    public void setDacoity(Integer dacoity) {
        this.dacoity = dacoity;
    }

    public Integer getTheft() {
        return theft;
    }

    public void setTheft(Integer theft) {
        this.theft = theft;
    }

    public Integer getHouseBreaking() {
        return houseBreaking;
    }

    public void setHouseBreaking(Integer houseBreaking) {
        this.houseBreaking = houseBreaking;
    }

    public Integer getVehicleTheft() {
        return vehicleTheft;
    }

    public void setVehicleTheft(Integer vehicleTheft) {
        this.vehicleTheft = vehicleTheft;
    }

    public Integer getAssault() {
        return assault;
    }

    public void setAssault(Integer assault) {
        this.assault = assault;
    }

    public Integer getCrimesAgainstWomen() {
        return crimesAgainstWomen;
    }

    public void setCrimesAgainstWomen(Integer crimesAgainstWomen) {
        this.crimesAgainstWomen = crimesAgainstWomen;
    }

    public Integer getCyberCrime() {
        return cyberCrime;
    }

    public void setCyberCrime(Integer cyberCrime) {
        this.cyberCrime = cyberCrime;
    }

    public Integer getKidnapping() {
        return kidnapping;
    }

    public void setKidnapping(Integer kidnapping) {
        this.kidnapping = kidnapping;
    }

    public Integer getRape() {
        return rape;
    }

    public void setRape(Integer rape) {
        this.rape = rape;
    }

    public Integer getMolestation() {
        return molestation;
    }

    public void setMolestation(Integer molestation) {
        this.molestation = molestation;
    }

    public Integer getChainSnatching() {
        return chainSnatching;
    }

    public void setChainSnatching(Integer chainSnatching) {
        this.chainSnatching = chainSnatching;
    }

    public Integer getPickpocketing() {
        return pickpocketing;
    }

    public void setPickpocketing(Integer pickpocketing) {
        this.pickpocketing = pickpocketing;
    }

    public Integer getMissingPerson() {
        return missingPerson;
    }

    public void setMissingPerson(Integer missingPerson) {
        this.missingPerson = missingPerson;
    }

    public Integer getCheating() {
        return cheating;
    }

    public void setCheating(Integer cheating) {
        this.cheating = cheating;
    }

    public Integer getDrugRelatedCrime() {
        return drugRelatedCrime;
    }

    public void setDrugRelatedCrime(Integer drugRelatedCrime) {
        this.drugRelatedCrime = drugRelatedCrime;
    }
}