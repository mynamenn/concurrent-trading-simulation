package com.company;

import java.util.HashMap;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        StockExchange stockExchange = new StockExchange();
        Company google = new Company("Google", 1000, 1000, 1);
        Company facebook = new Company("Facebook", 2000000, 2000000, 1);
        Client lawrence = new Client("Lawrence", 1000000000, stockExchange);
        Client john = new Client("John", 1000000000, stockExchange);

        stockExchange.addClient(lawrence);
        stockExchange.addClient(john);
        stockExchange.registerCompany(google, (float) google.getAvailableShares());
        stockExchange.registerCompany(facebook, (float) facebook.getAvailableShares());

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
            System.out.println(company.getName() + " : " + company.getAvailableShares());
        }
        HashMap<Company, Float> lawShares = lawrence.getStocks();
        for (Company company : lawShares.keySet()) {
            System.out.println(company.getName() + " : " + company.getAvailableShares());
        }
    }
}
