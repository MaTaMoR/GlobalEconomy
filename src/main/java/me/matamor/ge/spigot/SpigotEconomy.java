package me.matamor.ge.spigot;

import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.database.DatabaseSettings;
import me.matamor.ge.shared.database.DatabaseType;
import me.matamor.ge.shared.economy.Economy;
import me.matamor.ge.shared.economy.SimpleEconomy;
import me.matamor.ge.shared.economy.database.EconomyDatabase;
import me.matamor.ge.shared.identifier.IdentifierManager;
import me.matamor.ge.shared.identifier.database.IdentifierDatabase;
import me.matamor.ge.shared.identifier.defaults.SimpleIdentifierManager;
import me.matamor.ge.spigot.config.IConfig;
import me.matamor.ge.spigot.vault.VaultInjector;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SpigotEconomy extends JavaPlugin implements EconomyPlugin {

    private IdentifierDatabase identifierDatabase;
    private IdentifierManager identifierManager;

    private EconomyDatabase economyDatabase;
    private Economy economy;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.identifierDatabase = new IdentifierDatabase(this, loadSettings("identifiersql.yml"));
        if (this.identifierDatabase.loadDatabase()) {
            this.identifierManager = new SimpleIdentifierManager(this);

            for (Player player : getServer().getOnlinePlayers()) {
                this.identifierManager.insert(player.getName(), player.getUniqueId());
            }

            this.economyDatabase = new EconomyDatabase(this, loadSettings("sql.yml"));
            if (this.economyDatabase.loadDatabase()) {
                this.economy = new SimpleEconomy(this, getConfig().getInt("Configuration.Limit", 2000000)); //2.000.000 by default, this is loaded from the config

                for (Player player : getServer().getOnlinePlayers()) {
                    this.economy.load(player.getUniqueId());
                }

                //Inject vault!
                if(getConfig().getBoolean("Configuration.InjectVaultEconomy")) {
                    if(getServer().getPluginManager().isPluginEnabled("Vault")) {
                        VaultInjector vaultInjector = new VaultInjector(this); //If we do it on a different class we won't throw an exception if Vault is not found
                        vaultInjector.inject();

                        if(!vaultInjector.isInjected()) {
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
        } else {
            getLogger().severe("Couldn't load Identifier Database, shutting down instance...");
            setEnabled(false);
        }
    }

    @Override
    public IdentifierDatabase getIdentifierDatabase() {
        return this.identifierDatabase;
    }

    @Override
    public IdentifierManager getIdentifierManager() {
        return this.identifierManager;
    }

    @Override
    public EconomyDatabase getEconomyDatabase() {
        return this.economyDatabase;
    }

    @Override
    public Economy getEconomy() {
        return this.economy;
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
        if(type == null) type = DatabaseType.SQLITE;

        return new DatabaseSettings(type, section.getString("IP"), section.getInt("Port"), section.getString("DatabaseName"), section.getString("Username"), section.getString("Password"));
    }
}
