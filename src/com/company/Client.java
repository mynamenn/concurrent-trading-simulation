package com.company;

import java.util.HashMap;

public class Client implements Runnable {
    private final int TRIGGER_POINT = 80000;

    private final float INCREASE_FACTOR = (float) 1.3;

    private final float DECREASE_FACTOR = (float) -0.2;

    private StockExchange stockExchange;

    private HashMap<Company, Float> shares;

    private String name;

    private float balance;

    private TestCases testCases = new TestCases();

    private static Object lock = new Object();

    public Client(String name, float balance, StockExchange stockExchange) {
        shares = new HashMap<>();
        this.name = name;
        this.balance = balance;
        this.stockExchange = stockExchange;
    }

    public Client() {
        shares = new HashMap<>();
        name = "TEST";
        balance = 0;
    }

    @Override
    public void run() {
        testCases.buyAndBuyLow();
    }

    // Increase price when there's number of stocks hits a target.
    public void increasePrice(Company company) {
        if (company.getAvailableShares() == TRIGGER_POINT) {
            synchronized (lock) {
                stockExchange.changePriceBy(company, (float) (company.getPrice() * INCREASE_FACTOR));
                lock.notifyAll();
            }
        }
    }

    // Decrease price when there's number of stocks hits a target.
    public void decreasePrice(Company company) {
        if (company.getAvailableShares() == TRIGGER_POINT) {
            synchronized (lock) {
                stockExchange.changePriceBy(company, (float) (company.getPrice() * DECREASE_FACTOR));
                lock.notifyAll();
            }
        }
    }

    public HashMap<Company, Float> getStocks() {
        return shares;
    }

    public synchronized void setStocks(Company company, float numberOfShares) {
        shares.put(company, numberOfShares);
        company.setAvailableShares(numberOfShares);
    }

    public boolean buy(Company company, float numberOfShares) {
        synchronized (Client.class) {
            float availableShares = company.getAvailableShares();
            float totalPrice = company.getPrice() * numberOfShares;
            // Check if balance is enough and if company has enough shares.
            if (balance >= totalPrice && availableShares >= numberOfShares) {
                setStocks(company, availableShares - numberOfShares);
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
                setStocks(company, company.getAvailableShares() + numberOfShares);
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
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            boolean isBought = buy(company, numberOfShares);
            System.out.println(isBought
                    ? name + " auto-bought " + company.getName()
                    : name + " failed to auto-buy " + company.getName());
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

    public synchronized boolean deposit(float amount) {
        balance += amount;
        return true;
    }

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
                for (int i=0; i<49999; i++) {
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
                for (int i=0; i<49999; i++) {
                    buy(company, 1);
                }
            }
        }

        // Expected output is 99998.
        private void buyAndSell() {
            System.out.println(name + "'s thread started for buy and sell.");
            for (Company company : shares.keySet()) {
                for (int i=0; i<49999; i++) {
                    buy(company, 1);
                    sell(company, 1);
                }
            }
        }

        // Expected output is 1, provided there's only 2 clients, else output is numClients - 1.
        private void buyAndSellHigh() {
            System.out.println(name + "'s thread started for buy and sell high.");
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

        // Expected output is 1 with 3 clients.
        private void buyAndBuyLow() {
            System.out.println(name + "'s thread started for buy and sell low.");
            for (Company company : shares.keySet()) {
                // John keeps buying shares while Lawrence wait for sellHigh.
                if (name.equals("John")) {
                    for (int i=0; i<99994; i++) {
                        buy(company, 1);
                        decreasePrice(company);
                    }
                } else {
                    buyLow(company, 1, (float) 0.9);
                }
            }
        }
    }
}
