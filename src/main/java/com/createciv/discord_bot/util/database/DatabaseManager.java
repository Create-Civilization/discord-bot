package com.createciv.discord_bot.util.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DatabaseManager {
    protected Connection connection;
    protected final String dbPath;
    protected final String dbDirectory = "storage";
    protected final String fileName;

    public DatabaseManager(String dbName){
        File dir = new File(dbDirectory);
        if(!dir.exists()){
            dir.mkdirs();
        }

        this.fileName = dbName + ".db";
        this.dbPath = "jdbc:sqlite:" + dbDirectory + "/" + this.fileName;
    }

    /**
     * Saves a DatabaseEntry into the sql database
     * @param databaseEntry database entry to store
     */
    public abstract void add(DatabaseEntry databaseEntry) throws SQLException;

    public boolean exists(){
        File dbFile = new File(dbDirectory + "/" + fileName);
        return dbFile.exists();
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(dbPath);
    }

    public void disconnect() throws SQLException{
        if(connection != null && !connection.isClosed()){
            connection.close();
        }
    }

    public abstract void initDatabase() throws SQLException;
}
