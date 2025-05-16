package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.DatabaseEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class ModerationEntry extends DatabaseEntry {
    public int id = 0;
    public String adminID;
    public String playerID;
    public UUID playerUUID;
    public String punishmentTypes;
    public String reason;
    public Timestamp createdAt;
    public Timestamp expiresAt;
    public boolean executedOnServer = false;

    public ModerationEntry(String adminID, String playerID, UUID playerUUID, String punishmentTypes, String reason, Timestamp expiresAt, boolean executedOnServer) {
        this.adminID = adminID;
        this.playerID = playerID;
        this.playerUUID = playerUUID;
        this.punishmentTypes = punishmentTypes;
        this.reason = reason;
        this.createdAt = Timestamp.from(Instant.now());
        this.expiresAt = expiresAt;
        this.executedOnServer = executedOnServer;
    }

    public ModerationEntry(int id, String adminID, String playerID, UUID playerUUID, String punishmentTypes, String reason, Timestamp createdAt, Timestamp expiresAt, boolean executedOnServer) {
        this.id = id;
        this.adminID = adminID;
        this.playerID = playerID;
        this.playerUUID = playerUUID;
        this.punishmentTypes = punishmentTypes;
        this.reason = reason;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.executedOnServer = executedOnServer;
    }

    public static ModerationEntry fromResultSet(ResultSet resultSet) throws SQLException {
        return new ModerationEntry(
                resultSet.getInt("id"),
                resultSet.getString("playerID"),
                resultSet.getString("adminID"),
                UUID.fromString(resultSet.getString("punishedUserMinecraftUUID")),
                resultSet.getString("punishmentType"),
                resultSet.getString("punishmentReason"),
                resultSet.getTimestamp("punishmentDate"),
                resultSet.getTimestamp("punishmentExpirationTime"),
                resultSet.getBoolean("executedOnServer")
        );
    }
}
