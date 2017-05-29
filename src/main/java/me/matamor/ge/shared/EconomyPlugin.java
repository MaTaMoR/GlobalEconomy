package me.matamor.ge.shared;

import me.matamor.ge.shared.economy.Economy;
import me.matamor.ge.shared.economy.database.EconomyDatabase;

import java.io.File;
import java.util.logging.Logger;

public interface EconomyPlugin {

    EconomyDatabase getEconomyDatabase();

    Economy getEconomy();

    File getDataFolder();

    Logger getLogger();

    void runAsync(Runnable runnable);

}
