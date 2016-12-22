package me.matamor.ge.shared.economy;

import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

public interface EconomyEntry {

    Economy getEconomy();

    UUID getUUID();

    boolean hasAccount(String account);

    double getBalance(String account);

    void setBalance(String account, double balance);

    void addBalance(String account, double balance);

    void removeBalance(String account, double balance);

    Set<Entry<String, Double>> getEntries();

}
