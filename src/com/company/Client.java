package com.company;

import java.util.HashMap;

public class Client implements Runnable {
    private HashMap<Company, Double> shares;

    private double balance;

    public Client(double balance) {
        shares = new HashMap<>();
        this.balance = balance;
    }

    @Override
    // Simulate buying of stocks.
    public void run() {
        for (Company company : shares.keySet()) {
            for (int i=0; i<999999; i++) {
                buy(company, 1);
            }
        }
    }

    public HashMap<Company, Double> getStocks() {
        return shares;
    }

    public boolean setStocks(Company company, double numberOfShares) {
        // Check if company is in Hash Map.
        if (!shares.containsKey(company)) {
            shares.put(company, numberOfShares);
            return true;
        } else if (shares.get(company) == 0) {
            return true;
        }
        return false;
    }

    public boolean buy(Company company, double numberOfShares) {
        synchronized (Client.class) {
            double availableShares = company.getAvailableShares();
            double totalPrice = company.getPrice() * numberOfShares;
            // Check if balance is enough and if company has enough shares.
            if (balance >= totalPrice && availableShares >= numberOfShares) {
                // Update availableShares in company.
                company.setAvailableShares(availableShares - numberOfShares);
                // Update company in Hash Map.
                if (shares.containsKey(company)) {
                    shares.put(company, shares.get(company) + numberOfShares);
                } else {
                    shares.put(company, numberOfShares);
                }
                balance -= totalPrice;
                return true;
            }
            return false;
        }
    }

    public boolean sell(Company company, double numberOfShares) {
        // Check if client has enough shares.
        if (shares.get(company) >= numberOfShares) {
            balance += company.getPrice() * numberOfShares;
            return true;
        }
        return false;
    }

    public boolean buyLow(Company company, double numberOfShares, double limit) {
        return false;
    }

    public boolean sellHigh(Company company, double numberOfShares, double limit) {
        return true;
    }

    public boolean deposit(double amount) {
        balance += amount;
        return true;
    }

    public boolean withdraw(double amount) {
        // Check if balance is >= amount.
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
