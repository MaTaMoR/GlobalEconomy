package me.matamor.ge.shared.economy.database;

import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.database.Database;
import me.matamor.ge.shared.database.DatabaseSettings;
import me.matamor.ge.shared.database.types.MySQL;
import me.matamor.ge.shared.economy.EconomyEntry;
import me.matamor.ge.shared.economy.SimpleEconomyEntry;
import me.matamor.ge.shared.utils.Callback;

import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;

public class EconomyDatabase {

    private final EconomyPlugin plugin;
    private final DatabaseSettings settings;

    private Database database;

    public EconomyDatabase(EconomyPlugin plugin, DatabaseSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public boolean loadDatabase() {
        try {
            this.database = this.settings.createDatabase(this.plugin);
            this.database.openConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS economy (uuid VARCHAR(36), account VARCHAR(32), balance double, primary key(uuid, account));");
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Couldn't create database", e);
        }

        return false;
    }

    private final String saveMySQL = "INSERT INTO economy (uuid, account, balance) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE balance = VALUES(balance);";
    private final String saveSQLite = "REPLACE INTO economy (uuid, account, balance) VALUES (?, ?, ?)";

    public void save(@NotNull EconomyEntry economyEntry) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement((database instanceof MySQL ? saveMySQL : saveSQLite))) {
            for (Entry<String, Double> entry : economyEntry.getEntries()) {
                statement.setString(1, economyEntry.getUUID().toString());
                statement.setString(2, entry.getKey());
                statement.setDouble(3, entry.getValue());

                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    public void saveAsync(@NotNull final EconomyEntry economyEntry ) {
        this.plugin.runAsync(() -> {
            try {
                save(economyEntry);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public EconomyEntry load(@NotNull UUID uuid) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("SELECT account,balance FROM economy WHERE uuid = ?;")) {
            statement.setString(1, uuid.toString());

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    Map<String, Double> entries = new HashMap<>();
                    entries.put(resultSet.getString("account"), resultSet.getDouble("balance"));

                    while (resultSet.next()) {
                        entries.put(resultSet.getString("account"), resultSet.getDouble("balance"));
                    }

                    return new SimpleEconomyEntry(this.plugin.getEconomy(), uuid, entries);
                }
            }
        }

        return null;
    }

    public void loadAsync(@NotNull final UUID uuid, @NotNull final Callback<EconomyEntry> callback) {
        this.plugin.runAsync(() -> {
            try {
                callback.done(load(uuid), null);
            } catch (SQLException e) {
                e.printStackTrace();
                callback.done(null, e);
            }
        });
    }

    public boolean saveEntry(@NotNull UUID uuid, @NotNull String account, @NotNull double balance) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement((database instanceof MySQL ? saveMySQL : saveSQLite))) {
            statement.setString(1, uuid.toString());
            statement.setString(2, account);
            statement.setDouble(3, balance);

            return statement.execute();
        }
    }

    public void saveEntryAsync(@NotNull final UUID uuid, @NotNull final String account, @NotNull final double balance, final Callback<Boolean> callback) {
        this.plugin.runAsync(() -> {
            try {
                boolean result = saveEntry(uuid, account, balance);
                if (callback != null) callback.done(result, null);
            } catch (SQLException e) {
                e.printStackTrace();
                if (callback != null) callback.done(null, e);
            }
        });
    }
}
