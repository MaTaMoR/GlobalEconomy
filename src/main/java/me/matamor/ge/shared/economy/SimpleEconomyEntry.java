package me.matamor.ge.shared.economy;

import lombok.Getter;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleEconomyEntry implements EconomyEntry {

    private final Map<String, Double> entries = new ConcurrentHashMap<>();

    @Getter
    private final Economy economy;

    private final UUID uuid;

    public SimpleEconomyEntry(Economy economy, UUID uuid) {
        this(economy, uuid, new HashMap<>());
    }

    public SimpleEconomyEntry(Economy economy, UUID uuid, Map<String, Double> defaults) {
        this.economy = economy;
        this.uuid = uuid;

        this.entries.putAll(defaults);
    }

    @Override
    public UUID getUUID() {
        return this.uuid;
    }

    @Override
    public boolean hasAccount(String account) {
        return this.entries.containsKey(account);
    }

    @Override
    public double getBalance(String account) {
        return this.entries.getOrDefault(account, (double) 0);
    }

    @Override
    public void setBalance(String account, double balance) {
        System.out.println(balance);
        System.out.println(this.economy.getLimit());

        if (balance < 0)  {
            balance = 0;
        } else if (balance > this.economy.getLimit()) {
            balance = this.economy.getLimit();
        }

        if (balance <= 0) {
            System.out.println("Balance set to '0' on account '" + account + "' from the user '" + this.uuid + "'");
        }

        this.entries.put(account, balance);
        this.economy.getPlugin().getEconomyDatabase().saveEntryAsync(this.uuid, account, balance, null);
    }

    @Override
    public void addBalance(String account, double balance) {
        setBalance(account, getBalance(account) + balance);
    }

    @Override
    public void removeBalance(String account, double balance) {
        setBalance(account, getBalance(account) - balance);
    }

    @Override
    public Set<Entry<String, Double>> getEntries() {
        return Collections.unmodifiableSet(this.entries.entrySet());
    }
}
