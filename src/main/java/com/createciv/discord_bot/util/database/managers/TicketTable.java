package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.TableManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketTable extends TableManager<TicketEntry> {

	@Override
	public void initTable() throws SQLException {
		connect();
		Statement statement = connection.createStatement();
		statement.execute(
			"CREATE TABLE IF NOT EXISTS tickets (" +
				"id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY," +
				"authorID TEXT NOT NULL," +
				"threadChannelID TEXT NOT NULL," +
				"embedMessageID TEXT NOT NULL," +
				"lastActivity TIMESTAMP DEFAULT NOW()" +
				")"
		);
		statement.close();
		disconnect();
	}

	@Override
	public void add(TicketEntry tableEntry) throws SQLException {
		connect();
		PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tickets (authorID, threadChannelID, embedMessageID, lastActivity) VALUES (?, ?, ?, ?)");
		preparedStatement.setString(1, tableEntry.getAuthorID());
		preparedStatement.setString(2, tableEntry.getThreadChannelID());
		preparedStatement.setString(3, tableEntry.getEmbedMessageID());
		if (tableEntry.getLastActivity() != null) preparedStatement.setTimestamp(4, tableEntry.getLastActivity());
		preparedStatement.execute();
		preparedStatement.close();
		disconnect();
	}
	public void remove(String authorID) throws SQLException {
		connect();
		try (PreparedStatement preparedStatement = connection.prepareStatement("DELETE FROM tickets WHERE authorID = ?")){
		preparedStatement.setString(1, authorID);
		preparedStatement.execute();
	}finally {
			disconnect();
		}
	}
	public TicketEntry getFromAuthorID(String authorID) throws SQLException {
		connect();
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from tickets WHERE authorID = ?");
		preparedStatement.setString(1,authorID);
		try (ResultSet resultSet = preparedStatement.executeQuery()){
			if (resultSet.next()){
				return new TicketEntry(resultSet);
			}
		} finally {
		disconnect();
	}
		return null;
	}
	public TicketEntry getFromThreadID(String threadID) throws SQLException {
		connect();
		PreparedStatement preparedStatement = connection.prepareStatement("SELECT * from tickets WHERE threadChannelID = ?");
		preparedStatement.setString(1,threadID);
		try (ResultSet resultSet = preparedStatement.executeQuery()){
			if (resultSet.next()){
				return new TicketEntry(resultSet);
			}
		} finally {
			disconnect();
		}
		return null;
	}
}
