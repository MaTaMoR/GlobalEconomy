package me.matamor.ge.spigot;

import lombok.Getter;
import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.database.DatabaseSettings;
import me.matamor.ge.shared.database.DatabaseType;
import me.matamor.ge.shared.economy.Economy;
import me.matamor.ge.shared.economy.SimpleEconomy;
import me.matamor.ge.shared.economy.database.EconomyDatabase;
import me.matamor.ge.spigot.config.IConfig;
import me.matamor.ge.spigot.events.Events;
import me.matamor.ge.spigot.vault.VaultInjector;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.logging.Level;

public class SpigotEconomy extends JavaPlugin implements EconomyPlugin {

    @Getter
    private static SpigotEconomy instance;

    @Getter
    private EconomyDatabase economyDatabase;

    @Getter
    private Economy economy;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        this.economyDatabase = new EconomyDatabase(this, loadSettings("sql.yml"));
        if (this.economyDatabase.loadDatabase()) {
            this.economy = new SimpleEconomy(this, getConfig().getInt("Configuration.Limit", 2000000)); //2.000.000 by default, this is loaded from the config

            getServer().getPluginManager().registerEvents(new Events(this), this);

            try {
                for (Player player : getServer().getOnlinePlayers()) {
                    this.economy.load(player.getUniqueId());
                }
            } catch (SQLException e) {
                getLogger().log(Level.SEVERE, "There was an error while loading Economy data!", e);
                getServer().getPluginManager().disablePlugin(this);
                return;
            }

            //Inject vault!
            if (getConfig().getBoolean("Configuration.InjectVaultEconomy")) {
                if (getServer().getPluginManager().isPluginEnabled("Vault")) {
                    VaultInjector vaultInjector = new VaultInjector(this); //If we do it on a different class we won't throw an exception if Vault is not found
                    vaultInjector.inject();

                    if (!vaultInjector.isInjected()) {
                        getLogger().severe("Couldn't inject Vault dependency");
                        setEnabled(false);
                    }
                } else {
                    getLogger().severe("Vault inject is enabled but Vault is missing.");
                    setEnabled(false);
                }
            }
        } else {
            getLogger().severe("Couldn't load Economy Database, shutting down instance...");
            setEnabled(false);
        }
    }

    @Override
    public void onDisable() {
        instance = null;
    }

    @Override
    public void runAsync(Runnable runnable) {
        getServer().getScheduler().runTaskAsynchronously(this, runnable);
    }

    private DatabaseSettings loadSettings(String file) {
        IConfig config = new IConfig(this, file);
        config.save();

        ConfigurationSection section = config.getConfigurationSection("SQL-Settings");

        DatabaseType type = DatabaseType.byName(section.getString("Type"));
        if (type == null) type = DatabaseType.SQLITE;

        return new DatabaseSettings(type, section.getString("IP"), section.getInt("Port"), section.getString("DatabaseName"), section.getString("Username"), section.getString("Password"));
    }
}
