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
                "discordIDs TEXT NOT NULL, " +       // JSON array of Discord IDs
                "uuids TEXT NOT NULL, " +            // JSON array of known MC UUIDs
                "punishments TEXT NOT NULL" +        // JSON array of punishment objects
                ")");

        statement.close();
        disconnect();
    }

    @Override
    public void add(DatabaseEntry databaseEntry) throws SQLException {

    }






}
