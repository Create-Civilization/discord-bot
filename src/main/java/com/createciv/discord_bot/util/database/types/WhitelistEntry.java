package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class WhitelistEntry extends TableEntry<WhitelistEntry> {

    public int id = 0;
    public UUID playerUUID;
    public String discordID;
    public Timestamp createdAt;

    public WhitelistEntry(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt("id");
        playerUUID = UUID.fromString(resultSet.getString("playerUUID"));
        createdAt = resultSet.getTimestamp("createdAt");
        discordID = resultSet.getString("discordID");
    }

    public WhitelistEntry(UUID playerUUID, String discordID) {
        this.playerUUID = playerUUID;
        this.discordID = discordID;
        this.createdAt = Timestamp.from(Instant.now());
    }

    public WhitelistEntry(int id, UUID playerUUID, String discordID, Timestamp timestamp) {
        this.id = id;
        this.playerUUID = playerUUID;
        this.discordID = discordID;
        this.createdAt = timestamp;
    }
}
