package me.matamor.ge.shared.database.types;

import me.matamor.ge.shared.database.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL extends Database {

    private final String user;
    private final String database;
    private final String password;
    private final int port;
    private final String hostname;

    public MySQL(String hostname, int port, String username, String password) {
        this(hostname, port, null, username, password);
    }

    public MySQL(String hostname, int port, String database, String user, String password) {
        this.hostname = hostname;
        this.port = port;
        this.database = database;
        this.user = user;
        this.password = password;
    }

    @Override
    public Connection openConnection() throws SQLException, ClassNotFoundException {
        if (checkConnection()) {
            return connection;
        }

        String connectionURL = "jdbc:mysql://" + this.hostname + ":" + this.port;
        if (database != null) {
            connectionURL = connectionURL + "/" + this.database;
        }

        connectionURL = connectionURL + "?autoReconnect=true&amp;useUnicode=true;characterEncoding=UTF-8";

        Class.forName("com.mysql.jdbc.Driver");
        connection = DriverManager.getConnection(connectionURL, this.user, this.password);
        return connection;
    }
}
