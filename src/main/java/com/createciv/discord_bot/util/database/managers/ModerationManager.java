package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.createciv.discord_bot.util.database.DatabaseManager;
import com.createciv.discord_bot.util.database.types.ModerationEntry;
import com.createciv.discord_bot.util.database.types.PunishmentEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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

        statement.execute("CREATE TABLE IF NOT EXISTS punishments (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "discordIDs TEXT NOT NULL, " +       // JSON array of Discord IDs
                "uuids TEXT NOT NULL, " +            // JSON array of known MC UUIDs
                "punishments TEXT NOT NULL" +        // JSON array of punishment objects
                ")");
        statement.close();
        disconnect();
    }

    /**
     * Adds a new ModerationEntry to the database. This method inserts data such as Discord IDs, UUIDs,
     * and a list of punishments as a JSON array into the corresponding database table.
     *
     * @param databaseEntry The ModerationEntry object containing the data to be inserted into the database.
     *                      This must be an instance of ModerationEntry.
     * @throws SQLException If a database access error occurs.
     */
    @Override
    public void add(DatabaseEntry databaseEntry) throws SQLException {
        ModerationEntry moderationEntry = (ModerationEntry) databaseEntry;
        connect();
        String sql = "INSERT INTO punishments (discordIDs, uuids, punishments) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        preparedStatement.setString(1, moderationEntry.discordIDs.toString());
        preparedStatement.setString(2, moderationEntry.uuids.toString());

        //Take the list of punishments and make it a json array
        JsonArray arrayOfPunishments = new JsonArray();
        for (PunishmentEntry punishmentEntry : moderationEntry.punishments) {
            JsonObject punishmentObject = punishmentEntry.toJson();
            arrayOfPunishments.add(punishmentObject);
        }
        preparedStatement.setString(3, arrayOfPunishments.toString());
        preparedStatement.executeUpdate();
        preparedStatement.close();
        disconnect();
    }

    /**
     * Fetches a {@link ModerationEntry} from the database using a Discord ID.
     * <p>
     * The method connects to the database, executes a prepared statement to
     * search for a record where the `discordIDs` field contains the specified
     * Discord ID, and returns the corresponding {@link ModerationEntry} if found.
     *
     * @param id the Discord ID to search for in the database
     * @return the {@link ModerationEntry} associated with the given Discord ID, or null if no matching entry is found
     * @throws SQLException if a database access error occurs
     */
    public ModerationEntry selectByDiscordID(String id) throws SQLException {
        connect();
        String sql = "SELECT * FROM punishments WHERE discordIDs LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "%" + id + "%");
        ResultSet resultSet = preparedStatement.executeQuery();
        ModerationEntry toReturn = resultSet.next() ? ModerationEntry.fromResultSet(resultSet) : null;
        resultSet.close();
        preparedStatement.close();
        disconnect();
        return toReturn;
    }

    /**
     * Deletes a punishment record from the database based on the given Discord ID.
     * The method searches for entries in the "punishments" table where the
     * "discordIDs" column contains the specified ID and removes them.
     *
     * @param id the Discord ID to look for when deleting punishment records
     * @throws SQLException if a database access error occurs
     */
    public void deleteByDiscordID(String id) throws SQLException {
        connect();
        String sql = "DELETE FROM punishments WHERE discordIDs LIKE ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1, "%" + id + "%");
        preparedStatement.executeUpdate();
        preparedStatement.close();
    }

    /**
     * Retrieves all moderation entries from the database.
     * Establishes a connection to the database, executes a query to fetch all records
     * from the "punishments" table, and converts the result set into a list of
     * {@code ModerationEntry} objects.
     *
     * @return a list of {@code ModerationEntry} objects representing all records
     * in the database.
     * @throws SQLException if a database access error occurs or the query execution fails.
     */
    public List<ModerationEntry> getAll() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM punishments");
        List<ModerationEntry> allModerationEntries = new ArrayList<>();
        while (resultSet.next()) {
            ModerationEntry moderationEntry = ModerationEntry.fromResultSet(resultSet);
            allModerationEntries.add(moderationEntry);
        }
        resultSet.close();
        statement.close();
        disconnect();
        return allModerationEntries;
    }

    /**
     * Retrieves a list of ModerationEntry objects that have at least one punishment
     * with an expired expiration timestamp.
     *
     * @return a list of ModerationEntry objects with expired punishments
     * @throws SQLException if an SQL database access error occurs
     */
    public List<ModerationEntry> getEntriesWithExpiredPunishments() throws SQLException {
        List<ModerationEntry> allPunishments = this.getAll();
        List<ModerationEntry> moderationEntriesWithExpiredPunishments = new ArrayList<>();
        //Check for expired punishments
        for (ModerationEntry moderationEntry : allPunishments) {
            List<PunishmentEntry> punishments = moderationEntry.punishments;
            for (PunishmentEntry punishmentEntry : punishments) {
                if (punishmentEntry.getExpirationTimestamp().after(new Timestamp(System.currentTimeMillis()))) {
                    //Entry Has Expired Punishments
                    moderationEntriesWithExpiredPunishments.add(moderationEntry);
                    break;
                }
            }
        }
        return moderationEntriesWithExpiredPunishments;
    }






}
