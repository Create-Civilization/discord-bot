package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.createciv.discord_bot.util.database.DatabaseManager;
import com.createciv.discord_bot.util.database.types.ModerationEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ModerationManager extends DatabaseManager {
    public ModerationManager() {
        super("moderation");
    }

    @Override
    public void initDatabase() throws SQLException {
        connect();
        Statement statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "adminID TEXT NOT NULL, " +
                "playerID TEXT NOT NULL," +
                "punishedUserMinecraftUUID TEXT NOT NULL, " +
                "punishmentType TEXT CHECK(punishmentType IN ('KICK', 'BAN', 'MUTE', 'TEMPBAN', 'WARN')) NOT NULL, " +
                "punishmentReason TEXT, " +
                "punishmentDate INTEGER DEFAULT (strftime('%s', 'now')), " +
                "punishmentExpirationTime INTEGER, " +
                "executedOnServer BOOLEAN NOT NULL" +
                ")");

        statement.close();
        disconnect();
    }

    @Override
    public void add(DatabaseEntry databaseEntry) throws SQLException {
        ModerationEntry moderationEntry = (ModerationEntry) databaseEntry;
        connect();
        String sql = "INSERT INTO punishments (adminID, playerID, punishedUserMinecraftUUID, punishmentType, punishmentReason, punishmentDate, punishmentExpirationTime, executedOnServer) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

        preparedStatement.setString(1, moderationEntry.adminID);
        preparedStatement.setString(2, moderationEntry.playerID);
        preparedStatement.setString(3, moderationEntry.playerUUID.toString());
        preparedStatement.setString(4, moderationEntry.punishmentTypes);
        preparedStatement.setString(5, moderationEntry.reason);
        preparedStatement.setLong(6, moderationEntry.createdAt.getTime() / 1000);
        preparedStatement.setLong(7, moderationEntry.expiresAt.getTime() / 1000);
        preparedStatement.setBoolean(8, moderationEntry.executedOnServer);

        preparedStatement.executeUpdate();
        preparedStatement.close();
        disconnect();
    }

    public void removeWithDiscordID(String discordID) throws SQLException {
        connect();
        String sql = "DELETE FROM punishments WHERE playerID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, discordID);
        preparedStatement.execute();
        preparedStatement.close();
        disconnect();
    }

    public ModerationEntry getWithUUID(String uuid) throws SQLException {
        connect();
        String sql = "SELECT * FROM punishments WHERE punishedUserMinecraftUUID = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, uuid);
        ResultSet resultSet = preparedStatement.executeQuery();
        ModerationEntry toReturn = resultSet.next() ? ModerationEntry.fromResultSet(resultSet) : null;
        resultSet.close();
        preparedStatement.close();
        disconnect();

        return toReturn;
    }

    public List<ModerationEntry> getExpiredPunishments() throws SQLException {
        long currentTime = System.currentTimeMillis();
        long timeInSeconds = currentTime / 1000;

        connect();
        String sql = "SELECT * FROM punishments WHERE punishmentExpirationTime < ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, timeInSeconds);
        ResultSet resultSet = statement.executeQuery();
        List<ModerationEntry> expiredPunishments = new ArrayList<>();
        while (resultSet.next()) {
            ModerationEntry punishment = ModerationEntry.fromResultSet(resultSet);
            expiredPunishments.add(punishment);
        }

        resultSet.close();
        statement.close();
        disconnect();

        return expiredPunishments;

    }

    public List<ModerationEntry> getAllUnExecutedPunishment() throws SQLException {
        connect();
        String sql = "SELECT * FROM punishments WHERE executedOnServer = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setBoolean(1, false);
        ResultSet resultSet = statement.executeQuery();
        List<ModerationEntry> unExecutedPunishments = new ArrayList<>();
        while (resultSet.next()) {
            ModerationEntry punishment = ModerationEntry.fromResultSet(resultSet);
            unExecutedPunishments.add(punishment);
        }
        resultSet.close();
        statement.close();
        disconnect();
        return unExecutedPunishments;
    }




}
