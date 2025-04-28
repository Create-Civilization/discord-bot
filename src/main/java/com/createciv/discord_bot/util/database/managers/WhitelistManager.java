package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.DatabaseManager;

import java.sql.SQLException;
import java.sql.Statement;

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
                "createdAt INTEGER DEFAULT (strftime('%s', 'now')), " +
                "bans INTEGER DEFAULT 0, " +
                "bannedAt INTEGER DEFAULT 0," +
                "bannedUntil INTEGER DEFAULT 0" +
                ")");

        statement.close();
        disconnect();

    }
}
