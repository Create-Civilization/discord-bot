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
    public String referralRzn;
    public Timestamp createdAt;

    public WhitelistEntry(ResultSet resultSet) throws SQLException {
        playerUUID = UUID.fromString(resultSet.getString("playerUUID"));
        createdAt = resultSet.getTimestamp("createdAt");
        discordID = resultSet.getString("discordID");
        referralRzn = resultSet.getString("referral");
    }

    public WhitelistEntry(UUID playerUUID, String discordID, String refer) {
        this.playerUUID = playerUUID;
        this.discordID = discordID;
        this.createdAt = Timestamp.from(Instant.now());
        this.referralRzn = refer;
    }

    public WhitelistEntry(UUID playerUUID, String discordID, String refer,Timestamp timestamp) {
        this.playerUUID = playerUUID;
        this.discordID = discordID;
        this.createdAt = timestamp;
        this.referralRzn = refer;
    }
}
