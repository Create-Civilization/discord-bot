package com.createciv.discord_bot.util.database;

import com.createciv.discord_bot.ConfigLoader;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class TableManager<T extends TableEntry> {
    protected Connection connection;

    public abstract void initTable() throws SQLException;

    public abstract void add(T tableEntry) throws SQLException;

    public boolean isConnectionGood(){
        try{
            if(connection == null || connection.isClosed()){
                return false;
            }
            return connection.isValid(5);
        } catch (SQLException e){
            return false;
        }
    }

    public void ensureConnection() throws SQLException{
        if(!isConnectionGood()){
            disconnect();
            connect();
        }
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection(DatabaseRegistry.getDbAddress(), ConfigLoader.DB_USER, ConfigLoader.DB_PASSWORD);
    }

    public void disconnect(){
        try{
            if(connection != null && !connection.isClosed()){
                connection.close();
            }
        } catch (SQLException e){

        }
    }
}