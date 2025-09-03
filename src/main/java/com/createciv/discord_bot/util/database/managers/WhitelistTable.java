package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.TableEntry;
import com.createciv.discord_bot.util.database.TableManager;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class WhitelistTable extends TableManager<WhitelistEntry> {

    @Override
    public void initTable() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS whitelists (" +
                        "playerUUID TEXT PRIMARY KEY NOT NULL," +
                        "discordID TEXT NOT NULL, " +
                        "createdAt INTEGER DEFAULT (strftime('%s', 'now')))"
        );

        statement.close();
        disconnect();
    }

    @Override
    public void add(WhitelistEntry tableEntry) throws SQLException {
        connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO whitelists (playerUUID, discordID) VALUES (?, ?)");){
            preparedStatement.setString(1, tableEntry.playerUUID.toString());
            preparedStatement.setString(2, tableEntry.discordID);
            preparedStatement.execute();
        } finally{
            disconnect();
        }
    }

    public void remove(int id) throws SQLException {
        connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM whitelists WHERE id = ?;")){
            preparedStatement.setInt(1, id);
            preparedStatement.execute();
        } finally{
            disconnect();
        }
    }

    public void remove(String discordID) throws SQLException {
        connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM whitelists WHERE discordID = ?;")){
            preparedStatement.setString(1, discordID);
            preparedStatement.execute();
        } finally{
            disconnect();
        }
    }

    public void remove(UUID playerUUID) throws SQLException {
        connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM whitelists WHERE playerUUID = ?;")){
            preparedStatement.setString(1, playerUUID.toString());
            preparedStatement.execute();
        } finally {
            disconnect();
        }
    }

    public WhitelistEntry get(int id) throws SQLException {
        connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelists WHERE id = ?")) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new WhitelistEntry(resultSet);
                }
            }
        } finally {
            disconnect();
        }
        return null;
    }

    public WhitelistEntry get(UUID playerUUID) throws SQLException {
        connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelists WHERE playerUUID = ?")) {
            preparedStatement.setString(1, playerUUID.toString());
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new WhitelistEntry(resultSet);
                }
            }
        } finally {
            disconnect();
        }
        return null;
    }

    public WhitelistEntry get(String discordID) throws SQLException{
        connect();
        try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelists WHERE discordID = ?")) {
            preparedStatement.setString(1, discordID);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new WhitelistEntry(resultSet);
                }
            }
        } finally {
            disconnect();
        }
        return null;
    }
}
