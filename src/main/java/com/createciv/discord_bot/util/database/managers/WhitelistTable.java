package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.TableManager;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;

public class WhitelistTable extends TableManager<WhitelistEntry> {

	@Override
	public void initTable() throws SQLException {
		connect();
		Statement statement = connection.createStatement();
		statement.execute(
			"CREATE TABLE IF NOT EXISTS whitelists (" +
				"playerUUID TEXT PRIMARY KEY NOT NULL," +
				"discordID TEXT NOT NULL, " +
				"referral TEXT NOT NULL, " +
				"createdAt BIGINT DEFAULT (EXTRACT(EPOCH FROM NOW())::BIGINT))"
		);
		statement.close();
		disconnect();
	}

	@Override
	public void add(WhitelistEntry tableEntry) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO whitelists (playerUUID, discordID,referral) VALUES (?, ?, ?)");) {
			preparedStatement.setString(1, tableEntry.getPlayerUUID().toString());
			preparedStatement.setString(2, tableEntry.getDiscordID());
			preparedStatement.setString(3, tableEntry.getReferralReason());
			preparedStatement.execute();
		} finally {
			disconnect();
		}
	}

	public void remove(int id) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM whitelists WHERE discordID = ?;")) {
			preparedStatement.setInt(1, id);
			preparedStatement.execute();
		} finally {
			disconnect();
		}
	}

	public void remove(String discordID) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM whitelists WHERE discordID = ?;")) {
			preparedStatement.setString(1, discordID);
			preparedStatement.execute();
		} finally {
			disconnect();
		}
	}

	public void remove(UUID playerUUID) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM whitelists WHERE playerUUID = ?;")) {
			preparedStatement.setString(1, playerUUID.toString());
			preparedStatement.execute();
		} finally {
			disconnect();
		}
	}

	public WhitelistEntry get(int id) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelists WHERE discordID = ?")) {
			preparedStatement.setInt(1, id);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return new WhitelistEntry(resultSet);
				}
			}
		} finally {
			disconnect();
		}
		return null;
	}

	public WhitelistEntry get(UUID playerUUID) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelists WHERE playerUUID = ?")) {
			preparedStatement.setString(1, playerUUID.toString());
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return new WhitelistEntry(resultSet);
				}
			}
		} finally {
			disconnect();
		}
		return null;
	}

	public WhitelistEntry get(String discordID) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM whitelists WHERE discordID = ?")) {
			preparedStatement.setString(1, discordID);
			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					return new WhitelistEntry(resultSet);
				}
			}
		} finally {
			disconnect();
		}
		return null;
	}
}