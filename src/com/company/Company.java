package com.company;

public class Company {
    private String name;

    private double totalNumberOfShares;

    private double availableNumberOfShares;

    private double price;

    public Company(String name, double totalNumberOfShares, double availableNumberOfShares, double price) {
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

    public synchronized void setTotalShares(double number) {
        this.totalNumberOfShares = number;
    }

    public double getTotalShares() {
        return totalNumberOfShares;
    }

    public synchronized void setAvailableShares(double number) {
        this.availableNumberOfShares = number;
    }

    public double getAvailableShares() {
        return availableNumberOfShares;
    }

    public double getPrice() {
        return price;
    }

    public synchronized void setPrice(double number) {
        price = number;
    }
}
