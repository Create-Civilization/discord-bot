package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.TableEntry;
import com.createciv.discord_bot.util.database.TableManager;
import com.createciv.discord_bot.util.database.types.UsernameCacheEntry;

import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UsernameCacheTable extends TableManager<UsernameCacheEntry> {


    @Override
    public void initTable() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS usernameCache (" +
                        "playerUUID TEXT PRIMARY KEY NOT NULL, " +
                        "username TEXT NOT NULL UNIQUE, " +
                        "expireTime INTEGER NOT NULL)"
        );
        statement.close();
        disconnect();

    }
    @Override
    public void add(UsernameCacheEntry tableEntry) throws SQLException {
        connect();
        try (PreparedStatement statement = connection.prepareStatement("INSERT INTO usernameCache (username, playerUUID, expireTime) VALUES (?, ?, ?)")){
            statement.setString(1, tableEntry.username);
            statement.setString(2, tableEntry.playerUUID.toString());
            statement.setTimestamp(3, tableEntry.expireTime);
            statement.execute();
        } finally{
            disconnect();
        }
    }

    public void remove(UUID playerUUID) throws SQLException {
        connect();
        try (PreparedStatement statement = connection.prepareStatement("DELETE FROM usernameCache WHERE playerUUID = ?")){
            statement.setString(1, playerUUID.toString());
            statement.execute();
        } finally{
            disconnect();
        }
    }

    public UsernameCacheEntry get(int id) throws SQLException {
        connect();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM usernameCache WHERE id = ?")){
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new UsernameCacheEntry(resultSet);
            }
        }
        return null;
    }

    public UsernameCacheEntry get(UUID playerUUID) throws SQLException {
        connect();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM usernameCache WHERE playerUUID = ?")){
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new UsernameCacheEntry(resultSet);
            }
        }
        return null;
    }

    public List<UsernameCacheEntry> getExpired() throws SQLException {
        connect();
        List<UsernameCacheEntry> entries = new ArrayList<>();
        try (PreparedStatement statement = connection.prepareStatement("SELECT * FROM usernameCache WHERE expireTime < ?")){
            statement.setTimestamp(1, Timestamp.from(Instant.now()));
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                UsernameCacheEntry entry = new UsernameCacheEntry(resultSet);
                entries.add(entry);
            }
        }
        disconnect();
        return entries;
    }

    public void updateUsername(UUID key, String username) throws SQLException {
        connect();
        try(PreparedStatement statement = connection.prepareStatement("UPDATE usernameCache SET username = ? WHERE playerUUID = ?")){
            statement.setString(1, username);
            statement.setString(2, key.toString());
            statement.execute();
        } finally{
            disconnect();
        }
    }




}
