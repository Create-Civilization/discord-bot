package com.createciv.discord_bot.util.database;

import com.createciv.discord_bot.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class TableManager<T extends TableEntry> {
    protected Connection connection;

    public abstract void initTable() throws SQLException;

    public abstract void add(T tableEntry) throws SQLException;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(DatabaseRegistry.getDbAddress(), ConfigLoader.DB_USER, ConfigLoader.DB_PASSWORD);
    }

    public void disconnect() throws SQLException{
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }
}