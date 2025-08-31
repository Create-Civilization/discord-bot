package com.createciv.discord_bot.util.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class TableManager {
    protected Connection connection;

    protected final String dbPath;
    protected final String dbDirectory = "storage";

    public TableManager(String dbPaths){
        dbPath = "jdbc:sqlite:storage/createcivilization.db";
    }

    public abstract void initDatabase() throws SQLException;

    /**
     * Saves a DatabaseEntry into the sql database
     * @param databaseEntry database entry to store
     */
    public abstract void add(DatabaseEntry databaseEntry) throws SQLException;

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(dbPath);
    }

    public void disconnect() throws SQLException{
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }
}
