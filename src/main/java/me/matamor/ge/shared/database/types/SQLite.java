package me.matamor.ge.shared.database.types;

import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.database.Database;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;

public class SQLite extends Database {

    private final EconomyPlugin plugin;
    private final String dbLocation;

    public SQLite(EconomyPlugin plugin, String dbLocation) {
        this.plugin = plugin;
        this.dbLocation = (dbLocation.endsWith(".db") ? dbLocation : dbLocation + ".db");
    }

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (checkConnection()) return connection;

        File dataFolder = new File(plugin.getDataFolder() + File.separator + "Databases");
        if (!dataFolder.exists()) dataFolder.mkdirs();

        File file = new File(dataFolder, dbLocation);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Couldn't create database", e);
            }
        }

        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:" + dataFolder + "/" + dbLocation);
        return connection;
    }
}