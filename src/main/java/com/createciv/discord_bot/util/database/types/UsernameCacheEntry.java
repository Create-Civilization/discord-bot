package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class UsernameCacheEntry extends TableEntry<UsernameCacheEntry> {

    public String username;
    public String playerUUID;
    public Timestamp expireTime;

    public UsernameCacheEntry(String username, String playerUUID, Timestamp expireTime) {
        this.username = username;
        this.playerUUID = playerUUID;
        this.expireTime = expireTime;
    }

    public UsernameCacheEntry(ResultSet resultSet) throws SQLException {
        username = resultSet.getString("username");
        playerUUID = resultSet.getString("playerUUID");
        expireTime = resultSet.getTimestamp("expireTime");
    }

}
