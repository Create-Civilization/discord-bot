package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class UsernameCacheEntry extends TableEntry<UsernameCacheEntry> {

    public String username;
    public UUID playerUUID;
    public Timestamp expireTime;

    public UsernameCacheEntry(String username, UUID playerUUID, Timestamp expireTime) {
        this.username = username;
        this.playerUUID = playerUUID;
        this.expireTime = expireTime;
    }

    public UsernameCacheEntry(String username, UUID playerUUID) {
        this.username = username;
        this.playerUUID = playerUUID;
        this.expireTime = new Timestamp(System.currentTimeMillis() + (ConfigLoader.USERNAME_CACHE_EXPIRY_TIME_SECONDS * 1000));
    }

    public UsernameCacheEntry(ResultSet resultSet) throws SQLException {
        username = resultSet.getString("username");
        playerUUID = UUID.fromString(resultSet.getString("playerUUID"));
        expireTime = resultSet.getTimestamp("expireTime");
    }

}
