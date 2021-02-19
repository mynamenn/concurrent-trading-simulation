package com.company;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        Company google = new Company("Google", 1000, 1000, 1);
        Company facebook = new Company("Facebook", 2000000, 2000000, 1);
        Client lawrence = new Client(1000000000);
        Client john = new Client(1000000000);

        StockExchange stockExchange = new StockExchange();
        stockExchange.addClient(lawrence);
        stockExchange.addClient(john);
        stockExchange.registerCompany(google, google.getAvailableShares());
        stockExchange.registerCompany(facebook, facebook.getAvailableShares());

        lawrence.buy(google, 1);
        john.buy(google,1);

        Thread lawThread = new Thread(lawrence);
        Thread johnThread = new Thread(john);

        lawThread.start();
        johnThread.start();
        lawThread.join();
        johnThread.join();

        HashMap<Company, Double> johnShares = john.getStocks();
        for (Company company : johnShares.keySet()) {
            System.out.println(company.getName() + " : " + company.getAvailableShares());
        }
        HashMap<Company, Double> lawShares = lawrence.getStocks();
        for (Company company : lawShares.keySet()) {
            System.out.println(company.getName() + " : " + company.getAvailableShares());
        }
    }
}
