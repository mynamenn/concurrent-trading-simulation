package com.company;

import java.util.ArrayList;
import java.util.HashMap;

public class StockExchange {
    private HashMap<Company, Float> companies;

    private ArrayList<Client> clients;

    public StockExchange() {
        companies = new HashMap<>();
        clients = new ArrayList<>();
    }

    public synchronized boolean registerCompany(Company company, float numberOfShares) {
        // Check if company is in Hash Map.
        if (!companies.containsKey(company)) {
            companies.put(company, numberOfShares);
            return true;
        }
        return false;
    }

    public synchronized boolean deregisterCompany(Company company) {
        // Check if company is in Hash Map.
        if (companies.containsKey(company)) {
            companies.remove(company);
            return true;
        }
        return false;
    }

    public synchronized boolean addClient(Client client) {
        // Check if client is in array list.
        if (!clients.contains(client)) {
            clients.add(client);
            return true;
        }
        return false;
    }

    public synchronized boolean removeClient(Client client) {
        // Check if client is in array list.
        if (clients.contains(client)) {
            clients.remove(client);
            return true;
        }
        return false;
    }

    public ArrayList<Client> getClients() {
        return clients;
    }

    public HashMap<Company, Float> getCompanies() {
        return companies;
    }

    public synchronized void setPrice(Company company, float price) {
        companies.put(company, price);
    }

    public synchronized void changePriceBy(Company company, float price) {
        companies.put(company, companies.get(company) + price);
    }
}
