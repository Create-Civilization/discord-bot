package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.DatabaseEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class WhitelistEntry extends DatabaseEntry {
    int id = 0;
    public UUID playerUUID;
    public String discordID;
    public String username;
    public String reason;
    public Timestamp createdAt;

    public WhitelistEntry(UUID playerUUID, String discordID, String username, String reason) {
        this.playerUUID = playerUUID;
        this.discordID = discordID;
        this.username = username;
        this.reason = reason;
        this.createdAt = Timestamp.from(Instant.now());
    }

    public WhitelistEntry(int id, UUID playerUUID, String discordID, String username, String reason, Timestamp timestamp) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.discordID = discordID;
        this.username = username;
        this.reason = reason;
        this.createdAt = timestamp;
    }

    public static WhitelistEntry fromResultSet(ResultSet resultSet) throws SQLException {
        return new WhitelistEntry(
                resultSet.getInt("id"),
                UUID.fromString(resultSet.getString("playerUUID")),
                resultSet.getString("discordID"),
                resultSet.getString("username"),
                resultSet.getString("reason"),
                resultSet.getTimestamp("createdAt")
            );
    }
}
