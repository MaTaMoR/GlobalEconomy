package me.matamor.ge.shared.database;

import me.matamor.ge.shared.EconomyPlugin;
import me.matamor.ge.shared.database.types.MySQL;
import me.matamor.ge.shared.database.types.SQLite;

public class DatabaseSettings {

    private DatabaseType type;

    private final String ip;
    private final int port;
    private final String databaseName;
    private final String userName;
    private final String password;

    public DatabaseSettings(DatabaseType type, String ip, int port, String databaseName, String userName, String password) {
        this.type = type;
        this.ip = ip;
        this.port = port;
        this.databaseName = databaseName;
        this.userName = userName;
        this.password = password;
    }

    public DatabaseType getType() {
        return type;
    }

    public void setType(DatabaseType type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public Database createDatabase(EconomyPlugin plugin) {
        Database database;

        if(getType() == DatabaseType.MYSQL) {
            database = new MySQL(getIp(), getPort(), getDatabaseName(), getUserName(), getPassword());
        } else{
            database = new SQLite(plugin, getDatabaseName());
        }

        return database;
    }
}
