package com.company;

import java.util.HashMap;

public class Client implements Runnable {
    private StockExchange stockExchange;

    private HashMap<Company, Float> shares;

    private String name;

    private float balance;

    public Client(String name, float balance, StockExchange stockExchange) {
        shares = new HashMap<>();
        this.name = name;
        this.balance = balance;
        this.stockExchange = stockExchange;
    }

    @Override
    // Simulate buying of stocks.
    public void run() {
        System.out.println(name + "'s thread started.");
        for (Company company : shares.keySet()) {
            // John buy more shares.
            if (name.equals("John")) {
                for (int i=0; i<5; i++) {
                    buy(company, 1);
//                try {
//                    int randomNum = (int) (Math.random() * 100);
//                    Thread.sleep(randomNum);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                sell(company, 1);
                }
            } else {
                sellHigh(company, 1, (float) 1.3);
            }
        }
    }

    public HashMap<Company, Float> getStocks() {
        return shares;
    }

    // Instance level synchronization.
    public synchronized boolean setStocks(Company company, float numberOfShares) {
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
    public boolean buy(Company company, float numberOfShares) {
        // Class level synchronization.
        synchronized (Client.class) {
            float availableShares = company.getAvailableShares();
            float totalPrice = company.getPrice() * numberOfShares;
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
                stockExchange.changePriceBy(company, (float) (company.getPrice() * 0.1));
                return true;
            }
            return false;
        }
    }

    // Stock price decreases with each sell.
    public boolean sell(Company company, float numberOfShares) {
        // Class level synchronization.
        synchronized (Client.class) {
            // Check if client has enough shares.
            if (shares.get(company) >= numberOfShares) {
                company.setAvailableShares(company.getAvailableShares() + numberOfShares);
                balance += company.getPrice() * numberOfShares;
                // Decrease stock price by 10 percent.
                stockExchange.changePriceBy(company, (float) (company.getPrice() * 0.9));
                return true;
            }
            return false;
        }
    }

    // Buy the stock when its price hits the limit.
    public synchronized boolean buyLow(Company company, float numberOfShares, float limit) {
        while(company.getPrice() > limit) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean isBought = buy(company, numberOfShares);
        System.out.println(isBought? name + " auto-bought " + company.getName(): name + " failed to auto-buy " + company.getName());
        notifyAll();
        return isBought;
    }

    // Sell the stock when its price hits the limit.
    public synchronized boolean sellHigh(Company company, float numberOfShares, float limit) {
        System.out.println(name + " created sellHigh");
        while(company.getPrice() < limit) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        boolean isSold = sell(company, numberOfShares);
        System.out.println(isSold? name + " auto-sold " + company.getName() + " for " + company.getPrice(): name + " failed to auto-sell " + company.getName());
        notifyAll();
        return isSold;
    }

    // Instance level synchronization.
    public synchronized boolean deposit(float amount) {
        balance += amount;
        return true;
    }

    // Instance level synchronization.
    public synchronized boolean withdraw(float amount) {
        // Check if balance is >= amount.
        if (balance >= amount) {
            balance -= amount;
            return true;
        }
        return false;
    }
}
