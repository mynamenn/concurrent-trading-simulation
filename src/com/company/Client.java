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
        boolean autoBuy = false;
        boolean autoSell = false;
        for (Company company : shares.keySet()) {
            for (int i=0; i<499; i++) {
                if (!autoSell && sellHigh(company, 1, 1.3)) {
                    autoSell = true;
                }
                if (!autoBuy && buyLow(company, 1, 0.8)) {
                    autoBuy = true;
                }
                buy(company, 1);
//                try {
//                    int randomNum = (int) (Math.random() * 100);
//                    Thread.sleep(randomNum);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                sell(company, 1);
            }
        }
    }

    public HashMap<Company, Double> getStocks() {
        return shares;
    }

    // Instance level synchronization.
    public synchronized boolean setStocks(Company company, double numberOfShares) {
        // Check if company is in Hash Map.
        if (!shares.containsKey(company)) {
            shares.put(company, numberOfShares);
            return true;
        } else if (shares.get(company) == 0) {
            return true;
        }
        return false;
    }

    // Stock price increases for each buy.
    public boolean buy(Company company, double numberOfShares) {
        // Class level synchronization.
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
                // Increase stock price by 10 percent.
                company.setPrice(company.getPrice() * 1.1);
                return true;
            }
            return false;
        }
    }

    // Stock price decreases with each sell.
    public boolean sell(Company company, double numberOfShares) {
        // Class level synchronization.
        synchronized (Client.class) {
            // Check if client has enough shares.
            if (shares.get(company) >= numberOfShares) {
                company.setAvailableShares(company.getAvailableShares() + numberOfShares);
                balance += company.getPrice() * numberOfShares;
                // Decrease stock price by 10 percent.
                company.setPrice(company.getPrice() * 0.9);
                return true;
            }
            return false;
        }
    }

    // Buy the stock when its price hits the limit.
    public boolean buyLow(Company company, double numberOfShares, double limit) {
        if (company.getPrice() <= limit) {
            return buy(company, numberOfShares);
        }
        return false;
    }

    // Sell the stock when its price hits the limit.
    public boolean sellHigh(Company company, double numberOfShares, double limit) {
        if (company.getPrice() >= limit) {
            return sell(company, numberOfShares);
        }
        return false;
    }

    // Instance level synchronization.
    public synchronized boolean deposit(double amount) {
        balance += amount;
        return true;
    }

    // Instance level synchronization.
    public synchronized boolean withdraw(double amount) {
        // Check if balance is >= amount.
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
