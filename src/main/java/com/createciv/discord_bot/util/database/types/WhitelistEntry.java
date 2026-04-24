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
	private final Boolean active;
	private final Integer entryID;

	public WhitelistEntry(ResultSet resultSet) throws SQLException {
		playerUUID = UUID.fromString(resultSet.getString("playerUUID"));
		long createdAtSeconds = resultSet.getLong("createdAt");
		createdAt = new Timestamp(createdAtSeconds * 1000);
		discordID = resultSet.getString("discordID");
		referralReason = resultSet.getString("referral");
		active = resultSet.getBoolean("active");
		entryID = resultSet.getInt("ID");
	}

	WhitelistEntry(Builder builder) {
		playerUUID = builder.playerUUID;
		discordID = builder.discordID;
		referralReason = builder.referralReason;
		createdAt = builder.createdAt;
		active = builder.active;
		entryID = builder.entryID;
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

	public Boolean getActive(){
		return active;
	}
	public Integer getEntryID(){
		return entryID;
	}


	public static class Builder {
		private Boolean active;
		private UUID playerUUID;
		private String discordID;
		private String referralReason;
		private Timestamp createdAt;
		private Integer entryID;

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

		public Builder active(Boolean active) {
			this.active = active;
			return this;
		}

		public WhitelistEntry build() {
			if (discordID == null || playerUUID == null) throw new IllegalStateException("discordID or playerUUID is null");
			if (createdAt == null) this.createdAt = Timestamp.from(Instant.now());
			return new WhitelistEntry(this);
		}
	}
}