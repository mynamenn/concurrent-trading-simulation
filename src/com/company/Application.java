package com.company;

import java.util.HashMap;

public class Application {
    // Creates 3 threads, each thread corresponding to a Client instance.
    public static void simulate3Clients() throws InterruptedException {
        StockExchange stockExchange = new StockExchange();
        Company google = new Company("Google", 100000, 100000, 1);
        Client lawrence = new Client("Lawrence", 1000000000, stockExchange);
        Client john = new Client("John", 1000000000, stockExchange);
        Client max = new Client("Max", 1000000000, stockExchange);

        stockExchange.addClient(lawrence);
        stockExchange.addClient(john);
        stockExchange.addClient(max);
        stockExchange.registerCompany(google, google.getTotalShares());

        lawrence.buy(google, 1);
        john.buy(google,1);
        max.buy(google,1);
        Thread lawThread = new Thread(lawrence);
        Thread johnThread = new Thread(john);
        Thread maxThread = new Thread(max);

        lawThread.start();
        johnThread.start();
        maxThread.start();
        lawThread.join();
        johnThread.join();
        maxThread.join();

        // 100000(availableShares initially) - 99994(John bought 99994 shares) - 3(each client bought 1 share initially) - 2(shares bought from buyLow)
        // Thus, 100000 - 99999 = 1.
        System.out.println("");
        System.out.println(google.getName() + "'s available shares should be equal to 1.");
        System.out.println("availableShares equals 1: " + (google.getAvailableShares() == 1));
    }

    public static void simulate2Clients() throws InterruptedException {
        StockExchange stockExchange = new StockExchange();
        Company google = new Company("Google", 100000, 100000, 1);
        Client lawrence = new Client("Lawrence", 1000000000, stockExchange);
        Client john = new Client("John", 1000000000, stockExchange);

        stockExchange.addClient(lawrence);
        stockExchange.addClient(john);
        stockExchange.registerCompany(google, google.getAvailableShares());

        lawrence.buy(google, 1);
        john.buy(google,1);
        Thread lawThread = new Thread(lawrence);
        Thread johnThread = new Thread(john);

        lawThread.start();
        johnThread.start();
        lawThread.join();
        johnThread.join();

        HashMap<Company, Float> johnShares = john.getStocks();
        for (Company company : johnShares.keySet()) {
            System.out.println(company.getName() + "'s shares left: " + company.getAvailableShares());
        }
        HashMap<Company, Float> lawShares = lawrence.getStocks();
        for (Company company : lawShares.keySet()) {
            System.out.println(company.getName() + "'s shares left: " + company.getAvailableShares());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        simulate3Clients();
    }
}
