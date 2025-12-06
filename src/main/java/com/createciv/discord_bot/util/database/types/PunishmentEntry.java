package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

public class PunishmentEntry extends TableEntry<PunishmentEntry> {

	private final String discordID;
	private final String playerUUID;
	private final String punishmentReason;
	private final int punishmentType;
	private final Timestamp punishmentExpiration;
	private final String punishmentLocation;
	private final String adminID;

	public PunishmentEntry(ResultSet resultSet) throws SQLException {
		discordID = resultSet.getString("discordID");
		playerUUID = resultSet.getString("playerUUID");
		punishmentReason = resultSet.getString("punishmentReason");
		punishmentType = resultSet.getInt("punishmentType");
		punishmentExpiration = resultSet.getTimestamp("punishmentExpiration");
		punishmentLocation = resultSet.getString("punishmentLocation");
		adminID = resultSet.getString("adminID");
	}

	PunishmentEntry(Builder builder) {
		this.discordID = builder.discordID;
		this.playerUUID = builder.playerUUID;
		this.punishmentReason = builder.punishmentReason;
		this.punishmentType = builder.punishmentType;
		this.punishmentExpiration = builder.punishmentExpiration;
		this.punishmentLocation = builder.punishmentLocation;
		this.adminID = builder.adminID;
	}

	public String getDiscordID() {
		return discordID;
	}

	public String getPlayerUUID() {
		return playerUUID;
	}

	public String getPunishmentReason() {
		return punishmentReason;
	}

	public int getPunishmentType() {
		return punishmentType;
	}

	public Timestamp getPunishmentExpiration() {
		return punishmentExpiration;
	}

	public String getPunishmentLocation() {
		return punishmentLocation;
	}

	public String getAdminID() {
		return adminID;
	}

	public static class Builder {

		private String discordID;
		private String playerUUID;
		private String punishmentReason;
		private int punishmentType;
		private Timestamp punishmentExpiration;
		private String punishmentLocation;
		private String adminID;

		public Builder setDiscordID(String discordID) {
			this.discordID = discordID;
			return this;
		}

		public Builder setPlayerUUID(String playerUUID) {
			this.playerUUID = playerUUID;
			return this;
		}

		public Builder setPunishmentReason(String punishmentReason) {
			this.punishmentReason = punishmentReason;
			return this;
		}

		public Builder setPunishmentType(int punishmentType) {
			if (punishmentType == 1 || punishmentType == 2 || punishmentType == 3)
				throw new IllegalArgumentException("Invalid punishmentType values either 0,1,2");
			this.punishmentType = punishmentType;
			return this;
		}

		public Builder setPunishmentExpiration(Timestamp punishmentExpiration) {
			this.punishmentExpiration = punishmentExpiration;
			return this;
		}

		public Builder setPunishmentLocation(String punishmentLocation) {
			this.punishmentLocation = punishmentLocation;
			return this;
		}

		public Builder setAdminID(String adminID) {
			this.adminID = adminID;
			return this;
		}

		public PunishmentEntry build() {
			return new PunishmentEntry(this);
		}
	}
}