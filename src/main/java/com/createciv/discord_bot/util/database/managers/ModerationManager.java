package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.createciv.discord_bot.util.database.DatabaseManager;
import com.createciv.discord_bot.util.database.types.ModerationEntry;
import com.createciv.discord_bot.util.database.types.PunishmentEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A class responsible for managing a SQLite database that stores moderation-related data,
 * including punishments associated with specific Discord IDs and Minecraft UUIDs.
 * Extends the abstract DatabaseManager class and provides concrete implementations
 * for initializing the database, adding entries, retrieving entries, deleting entries,
 * and fetching entries with specific conditions.
 */
public class ModerationManager extends DatabaseManager {

    public ModerationManager() {
        super("moderation");
    }

    @Override
    public void initDatabase() throws SQLException {
        connect();
        Statement statement = connection.createStatement();


        //HOLY FUCK THIS IS OUT OF MY LEAGUE

        statement.execute("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT)");

        statement.execute("CREATE TABLE IF NOT EXISTS user_discord_ids (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "discord_id TEXT NOT NULL UNIQUE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

        statement.execute("CREATE TABLE IF NOT EXISTS user_uuids(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "uuid TEXT NOT NULL UNIQUE," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");

        statement.execute("CREATE TABLE IF NOT EXISTS punishments(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "user_id INTEGER NOT NULL," +
                "admin_id INTEGER NOT NULL," +
                "punishmentType TEXT NOT NULL," +
                "reason TEXT," +
                "experationTimestamp INTEGER NOT NULL," +
                "createdTimestamp INTEGER NOT NULL," +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE)");


        statement.close();
        disconnect();
    }


    @Override
    public void add(DatabaseEntry databaseEntry) throws SQLException {

    }


}
