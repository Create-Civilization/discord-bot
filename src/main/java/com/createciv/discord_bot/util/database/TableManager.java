package com.createciv.discord_bot.util.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class TableManager<T extends TableEntry> {
    protected Connection connection;

    protected final String dbPath = "jdbc:sqlite:storage/createcivilization.db";

    public abstract void initTable() throws SQLException;

    public abstract void add(T tableEntry) throws SQLException;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(dbPath);
    }

    public void disconnect() throws SQLException{
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }
}
