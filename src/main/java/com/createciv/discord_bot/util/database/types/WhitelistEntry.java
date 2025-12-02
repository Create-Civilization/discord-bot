package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public class WhitelistEntry extends TableEntry<WhitelistEntry> {

    private final UUID playerUUID;
    private final String discordID;
    private final String referralReason;
    private final Timestamp createdAt;

    public WhitelistEntry(ResultSet resultSet) throws SQLException {
        playerUUID = UUID.fromString(resultSet.getString("playerUUID"));
        createdAt = resultSet.getTimestamp("createdAt");
        discordID = resultSet.getString("discordID");
        referralReason = resultSet.getString("referral");
    }

    WhitelistEntry(Builder builder){
        playerUUID = builder.playerUUID;
        discordID = builder.discordID;
        referralReason = builder.referralReason;
        createdAt = builder.createdAt;
    }

    public UUID getPlayerUUID() {
        return playerUUID;
    }
    public String getDiscordID() {
        return discordID;
    }
    public String getReferralReason() {
        return referralReason;
    }
    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public static class Builder {
        private UUID playerUUID;
        private String discordID;
        private String referralReason;
        private Timestamp createdAt;

        public Builder playerUUID(UUID playerUUID) {
            this.playerUUID = playerUUID;
            return this;
        }

        public Builder discordID(String discordID) {
            this.discordID = discordID;
            return this;
        }

        public Builder referralReason(String referralReason) {
            this.referralReason = referralReason;
            return this;
        }

        public Builder createdAt(Timestamp createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public WhitelistEntry build() {
            if(discordID == null || playerUUID == null) throw new IllegalStateException("discordID or playerUUID is null");
            if(createdAt == null) this.createdAt = Timestamp.from(Instant.now());
            return new WhitelistEntry(this);
        }




    }
}
