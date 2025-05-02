package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.createciv.discord_bot.util.database.DatabaseManager;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WhitelistManager extends DatabaseManager {
    public WhitelistManager() {
        super("whitelist");
    }

    @Override
    public void initDatabase() throws SQLException {
        connect();
        Statement statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS whitelistData (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "playerUUID TEXT NOT NULL UNIQUE, " +
                "discordID TEXT NOT NULL UNIQUE, " +
                "username TEXT NOT NULL, " +
                "reason TEXT NOT NULL, " +
                "referral TEXT," +
                "createdAt INTEGER DEFAULT (strftime('%s', 'now')) " +
                ")");

        statement.close();
        disconnect();
    }

    @Override
    public void add(DatabaseEntry databaseEntry) throws SQLException {
        WhitelistEntry whitelistEntry = (WhitelistEntry) databaseEntry;
        connect();

        String sql = "INSERT INTO whitelistData (playerUUID, discordID, username, reason, referral, createdAt) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, whitelistEntry.playerUUID.toString());
        preparedStatement.setString(2, whitelistEntry.discordID);
        preparedStatement.setString(3, whitelistEntry.username);
        preparedStatement.setString(4, whitelistEntry.reason);
        preparedStatement.setString(5, whitelistEntry.referral);
        preparedStatement.setTimestamp(6, whitelistEntry.createdAt);

        preparedStatement.executeUpdate();

        disconnect();
    }

    public void removeWithDiscordID(String discordID) throws SQLException {
        connect();
        String sql = "DELETE FROM whitelistData WHERE discordID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, discordID);
        preparedStatement.execute();
        preparedStatement.close();

        disconnect();
    }

    public WhitelistEntry getWithDiscordID(String id) throws SQLException {
        connect();

        String sql = "SELECT * FROM whitelistData WHERE discordID = ?";

        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, id);
        ResultSet resultSet = preparedStatement.executeQuery();
        WhitelistEntry toReturn = resultSet.next() ? WhitelistEntry.fromResultSet(resultSet) : null;
        resultSet.close();
        preparedStatement.close();

        disconnect();
        return toReturn;
    }

    public List<WhitelistEntry> getAll() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM whitelistData");
        List<WhitelistEntry> allWhitelistEntries = new ArrayList<>();
        while (resultSet.next()) {
            WhitelistEntry whitelistEntry = WhitelistEntry.fromResultSet(resultSet);
            allWhitelistEntries.add(whitelistEntry);
        }

        resultSet.close();
        statement.close();
        disconnect();

        return allWhitelistEntries;
    }

    public void update(WhitelistEntry entry) throws SQLException {
        connect();
        String sql = "UPDATE whitelistData SET discordID = ?, username = ?, reason = ?, referral = ? WHERE playerUUID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, entry.discordID);
        preparedStatement.setString(2, entry.username);
        preparedStatement.setString(3, entry.reason);
        preparedStatement.setString(4, entry.referral);
        preparedStatement.setString(5, entry.playerUUID.toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        disconnect();
    }
}
