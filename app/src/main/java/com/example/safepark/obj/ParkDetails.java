package com.example.safepark.obj;

public class ParkDetails {
    private String parkName, parkAddress;
    private double lat,lon;
    private int
            carSlots, carPerHourCost,
            lorrySlots, lorryPerHourCost,
            bikeSlots, bikePerHourCost,
            vanSlots, vanPerHourCost;

    public ParkDetails() {
    }

    public ParkDetails(String parkName, String parkAddress, double lat, double lon, int carSlots, int carPerHourCost, int lorrySlots, int lorryPerHourCost, int bikeSlots, int bikePerHourCost, int vanSlots, int vanPerHourCost) {
        this.lat = lat;
        this.lon = lon;
        this.parkName = parkName;
        this.parkAddress = parkAddress;
        this.carSlots = carSlots;
        this.carPerHourCost = carPerHourCost;
        this.lorrySlots = lorrySlots;
        this.lorryPerHourCost = lorryPerHourCost;
        this.bikeSlots = bikeSlots;
        this.bikePerHourCost = bikePerHourCost;
        this.vanSlots = vanSlots;
        this.vanPerHourCost = vanPerHourCost;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getParkName() {
        return parkName;
    }

    public void setParkName(String parkName) {
        this.parkName = parkName;
    }

    public String getParkAddress() {
        return parkAddress;
    }

    public void setParkAddress(String parkAddress) {
        this.parkAddress = parkAddress;
    }

    public int getCarSlots() {
        return carSlots;
    }

    public void setCarSlots(int carSlots) {
        this.carSlots = carSlots;
    }

    public int getCarPerHourCost() {
        return carPerHourCost;
    }

    public void setCarPerHourCost(int carPerHourCost) {
        this.carPerHourCost = carPerHourCost;
    }

    public int getLorrySlots() {
        return lorrySlots;
    }

    public void setLorrySlots(int lorrySlots) {
        this.lorrySlots = lorrySlots;
    }

    public int getLorryPerHourCost() {
        return lorryPerHourCost;
    }

    public void setLorryPerHourCost(int lorryPerHourCost) {
        this.lorryPerHourCost = lorryPerHourCost;
    }

    public int getBikeSlots() {
        return bikeSlots;
    }

    public void setBikeSlots(int bikeSlots) {
        this.bikeSlots = bikeSlots;
    }

    public int getBikePerHourCost() {
        return bikePerHourCost;
    }

    public void setBikePerHourCost(int bikePerHourCost) {
        this.bikePerHourCost = bikePerHourCost;
    }

    public int getVanSlots() {
        return vanSlots;
    }

    public void setVanSlots(int vanSlots) {
        this.vanSlots = vanSlots;
    }

    public int getVanPerHourCost() {
        return vanPerHourCost;
    }

    public void setVanPerHourCost(int vanPerHourCost) {
        this.vanPerHourCost = vanPerHourCost;
    }
}
