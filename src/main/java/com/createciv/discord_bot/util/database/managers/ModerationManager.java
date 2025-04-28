package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.createciv.discord_bot.util.database.DatabaseManager;

import java.sql.SQLException;
import java.sql.Statement;

public class ModerationManager extends DatabaseManager {
    public ModerationManager() {
        super("moderation");
    }

    @Override
    public void add(DatabaseEntry databaseEntry) throws SQLException {

    }

    @Override
    public void initDatabase() throws SQLException {
        connect();
        Statement statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "adminID TEXT NOT NULL, " +
                "punishedUserMinecraft TEXT NOT NULL, " +
                "punishmentType TEXT CHECK(punishmentType IN ('KICK', 'BAN', 'MUTE', 'TEMPBAN', 'WARN')) NOT NULL, " +
                "punishmentReason TEXT, " +
                "punishmentDate INTEGER DEFAULT (strftime('%s', 'now')), " +
                "punishmentExpirationTime INTEGER" +
                ")");

        statement.close();
        disconnect();
    }
}
