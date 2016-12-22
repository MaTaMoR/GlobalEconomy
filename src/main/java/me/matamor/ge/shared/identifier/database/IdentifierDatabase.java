package me.matamor.ge.shared.identifier.database;

import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.database.Database;
import me.matamor.ge.shared.database.DatabaseSettings;
import me.matamor.ge.shared.utils.Callback;

import javax.validation.constraints.NotNull;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class IdentifierDatabase {

    private final EconomyPlugin plugin;
    private final DatabaseSettings settings;

    private Database database;

    public IdentifierDatabase(EconomyPlugin plugin, DatabaseSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public boolean loadDatabase() {
        try {
            this.database = this.settings.createDatabase(this.plugin);
            this.database.openConnection().createStatement().execute("CREATE TABLE IF NOT EXISTS uuids (name VARCHAR(16), uuid VARCHAR(36) PRIMARY KEY);");
            return true;
        } catch (SQLException | ClassNotFoundException e) {
            this.plugin.getLogger().log(Level.SEVERE, "Couldn't create database", e);
        }

        return false;
    }

    public boolean save(@NotNull String name, @NotNull UUID uuid) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("INSERT INTO uuids (name, uuid) VALUES (?, ?) ON DUPLICATE KEY UPDATE name = VALUES(name);")) {
            statement.setString(1, name);
            statement.setString(2, uuid.toString());

            return statement.execute();
        }
    }

    public void saveAsync(@NotNull final String name, @NotNull final UUID uuid, final Callback<Boolean> callback) {
        this.plugin.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean result = save(name, uuid);
                    if (callback != null) callback.done(result, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (callback != null) callback.done(null, e);
                }
            }
        });
    }

    public UUID load(@NotNull String name) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("SELECT uuids FROM uuids WHERE name = ?;")) {
            statement.setString(1, name);

            try(ResultSet resultSet = statement.executeQuery()) {
                if(resultSet.next()) {
                    return UUID.fromString(resultSet.getString("uuid"));
                }
            }
        }

        return null;
    }

    public void loadAsync(@NotNull final String name, @NotNull final Callback<UUID> callback) {
        this.plugin.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    callback.done(load(name), null);
                } catch (SQLException e) {
                    e.printStackTrace();
                    callback.done(null, e);
                }
            }
        });
    }

    public boolean remove(@NotNull String name) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("REMOVE FROM uuids WHERE name = ?;")) {
            statement.setString(1, name);

            return statement.execute();
        }
    }

    public boolean remove(@NotNull String name, @NotNull UUID uuid) throws SQLException {
        try (PreparedStatement statement = this.database.getConnection().prepareStatement("REMOVE FROM uuids WHERE name = ? AND uuid = ?;")) {
            statement.setString(1, name);
            statement.setString(2, uuid.toString());

            return statement.execute();
        }
    }

    public void removeAsync(@NotNull final String name, final Callback<Boolean> callback) {
        this.plugin.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean result = remove(name);
                    if (callback != null) callback.done(result, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (callback != null) callback.done(null, e);
                }
            }
        });
    }

    public void removeAsync(@NotNull final String name, @NotNull final UUID uuid, final Callback<Boolean> callback) {
        this.plugin.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    boolean result = remove(name, uuid);
                    if (callback != null) callback.done(result, null);
                } catch (SQLException e) {
                    e.printStackTrace();
                    if (callback != null) callback.done(null, e);
                }
            }
        });
    }
}
