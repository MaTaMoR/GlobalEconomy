package me.matamor.ge.shared.economy;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleEconomyEntry implements EconomyEntry {

    private final Map<String, Double> entries = new ConcurrentHashMap<>();

    private final Economy economy;
    private final UUID uuid;

    public SimpleEconomyEntry(Economy economy, UUID uuid) {
        this(economy, uuid, new HashMap<String, Double>());
    }

    public SimpleEconomyEntry(Economy economy, UUID uuid, Map<String, Double> defaults) {
        this.economy = economy;
        this.uuid = uuid;

        this.entries.putAll(defaults);
    }

    @Override
    public Economy getEconomy() {
        return economy;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public boolean hasAccount(String account) {
        return this.entries.containsKey(account);
    }

    @Override
    public double getBalance(String account) {
        return (hasAccount(account) ? this.entries.get(account) : 0);
    }

    @Override
    public void setBalance(String account, double balance) {
        if (balance < 0)  {
            balance = 0;
        } else if (balance > this.economy.getLimit()) {
            balance = this.economy.getLimit();
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
