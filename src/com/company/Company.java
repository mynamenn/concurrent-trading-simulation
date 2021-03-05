package com.company;

public class Company {
    private String name;

    private float totalNumberOfShares;

    private float availableNumberOfShares;

    private float price;

    public Company(String name, float totalNumberOfShares, float availableNumberOfShares, float price) {
        this.name = name;
        this.totalNumberOfShares = totalNumberOfShares;
        this.availableNumberOfShares = availableNumberOfShares;
        this.price = price;
    }

    public synchronized void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public synchronized void setTotalShares(float number) {
        this.totalNumberOfShares = number;
    }

    public float getTotalShares() {
        return totalNumberOfShares;
    }

    public synchronized void setAvailableShares(float number) {
        this.availableNumberOfShares = number;
    }

    public float getAvailableShares() {
        return availableNumberOfShares;
    }

    public float getPrice() {
        return price;
    }

    public synchronized void setPrice(float number) {
        price = number;
    }
}
