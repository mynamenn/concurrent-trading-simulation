package com.company;

import java.util.HashMap;

public class Client implements Runnable {
    private final int INCREASE_POINT = 80000;

    private final int INCREASE_FACTOR = 80000;

    private StockExchange stockExchange;

    private HashMap<Company, Float> shares;

    private String name;

    private float balance;

    private TestCases testCases = new TestCases();

    // Class level lock for sellHigh and buyLow.
    private static Object lock = new Object();

    public Client(String name, float balance, StockExchange stockExchange) {
        shares = new HashMap<>();
        this.name = name;
        this.balance = balance;
        this.stockExchange = stockExchange;
    }

    @Override
    // Simulate buying of stocks.
    public void run() {
        testCases.buyAndSellHigh();
    }

    public void increasePrice(Company company) {
        synchronized (lock) {
            // Increase price when there's number of stocks hit a certain number.
            if (company.getAvailableShares() == INCREASE_POINT) {
                stockExchange.changePriceBy(company, (float) (company.getPrice() * INCREASE_FACTOR));
            }
            lock.notifyAll();
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

    public boolean buy(Company company, float numberOfShares) {
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
                return true;
            }
            return false;
        }
    }

    public boolean sell(Company company, float numberOfShares) {
        synchronized (Client.class) {
            // Check if client has enough shares.
            if (shares.get(company) >= numberOfShares) {
                company.setAvailableShares(company.getAvailableShares() + numberOfShares);
                balance += company.getPrice() * numberOfShares;
                return true;
            }
            return false;
        }
    }

    // Buy the stock when its price hits the limit.
    public boolean buyLow(Company company, float numberOfShares, float limit) {
        synchronized (lock) {
            while(company.getPrice() > limit) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            boolean isBought = buy(company, numberOfShares);
            System.out.println(isBought? name + " auto-bought " + company.getName(): name + " failed to auto-buy " + company.getName());
            return isBought;
        }
    }

    // Sell the stock when its price hits the limit.
    public boolean sellHigh(Company company, float numberOfShares, float limit) {
        synchronized (lock) {
            System.out.println(name + " created sellHigh for " + company.getName() + ": " + limit);
            while (company.getPrice() < limit) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            boolean isSold = sell(company, numberOfShares);
            System.out.println(isSold
                    ? name + " auto-sold " + company.getName() + " for " + company.getPrice()
                    : name + " failed to auto-sell " + company.getName());
            return isSold;
        }
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

    class TestCases {
        // Expected output is 0.
        private void simulateBuy() {
            System.out.println(name + "'s thread started for buy.");
            for (Company company : shares.keySet()) {
                for (int i=0; i<49998; i++) {
                    buy(company, 1);
                }
            }
        }

        // Expected output is 0.
        private void sleepThenBuy() {
            System.out.println(name + "'s thread started for sleep then buy.");
            try {
                int randomNum = (int) (Math.random() * 100);
                Thread.sleep(randomNum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for (Company company : shares.keySet()) {
                for (int i=0; i<49998; i++) {
                    buy(company, 1);
                }
            }
        }

        // Expected output is 99998.
        private void buyAndSell() {
            System.out.println(name + "'s thread started for buy and sell.");
            for (Company company : shares.keySet()) {
                for (int i=0; i<49998; i++) {
                    buy(company, 1);
                    sell(company, 1);
                }
            }
        }

        // Expected output is 1, provided there's only 2 clients, else output is numClients - 1.
        private void buyAndSellHigh() {
            System.out.println(name + "'s thread started.");
            for (Company company : shares.keySet()) {
                // John keeps buying shares while Lawrence wait for sellHigh.
                if (name.equals("John")) {
                    for (int i=0; i<99998; i++) {
                        buy(company, 1);
                        increasePrice(company);
                    }
                } else {
                    sellHigh(company, 1, (float) INCREASE_FACTOR);
                }
            }
        }
    }
}
