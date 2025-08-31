package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.createciv.discord_bot.util.database.TableManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The TicketManager class is responsible for managing operations on tickets stored in a SQLite database.
 * It provides functionality for creating, retrieving, updating, and deleting ticket entries.
 * This class extends the DatabaseManager, which provides basic database connection handling.
 */
public class TicketManager extends TableManager {
    /**
     * Constructs a new instance of the TicketManager class.
     * <p>
     * This class extends the DatabaseManager class and is specifically designed
     * to handle and manage tickets within the database. It is initialized with
     * the database name "tickets" and is responsible for ticket-related operations,
     * including creating, retrieving, updating, and deleting ticket records.
     */
    public TicketManager() {
        super("tickets");
    }

    /**
     * Initializes the database for storing ticket data.
     * This method ensures that the required table for managing tickets is created if it does not already exist.
     *
     * The table created consists of the following columns:
     * - `id`: An auto-incrementing primary key for unique identification of tickets.
     * - `authorID`: The ID of the user who created the ticket. This column is mandatory.
     * - `threadChannelID`: The ID of the thread channel associated with the ticket. This column is mandatory.
     * - `embedMessageID`: The ID of the embed message associated with the ticket. This column is mandatory.
     * - `lastActivity`: An integer timestamp, set to the current Unix time by default, representing the last activity on the ticket.
     *
     * During the execution, the method establishes a connection to the database, creates the necessary table if it does not exist,
     * and closes the connection after completing the operation.
     *
     * @throws SQLException if there is an error in establishing a connection, creating the table, or executing the SQL statement.
     */
    @Override
    public void initDatabase() throws SQLException {
        connect();
        Statement statement = connection.createStatement();

        statement.execute(
            "CREATE TABLE IF NOT EXISTS tickets (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "authorID TEXT NOT NULL, " +
            "threadChannelID TEXT NOT NULL, " +
            "embedMessageID TEXT NOT NULL, " +
            "lastActivity INTEGER DEFAULT (strftime('%s', 'now'))" +
            ")"
        );

        statement.close();
        disconnect();

    }

    /**
     * Adds a new ticket entry into the database. This method inserts the fields of the provided
     * {@code TicketEntry} into the tickets table in the database.
     *
     * @param databaseEntry the {@code DatabaseEntry} to be added, which must be an instance of {@code TicketEntry}.
     *                      It contains the information about a ticket including author ID, thread channel ID,
     *                      and embed message ID.
     * @throws SQLException if a database access error occurs or the provided entry cannot be inserted.
     */
    @Override
    public void add(DatabaseEntry databaseEntry) throws SQLException{
        TicketEntry ticketEntry = (TicketEntry) databaseEntry;
        connect();

        String sql = "INSERT INTO tickets (authorID, threadChannelID, embedMessageID) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, ticketEntry.authorID);
        preparedStatement.setString(2, ticketEntry.threadChannelID);
        preparedStatement.setString(3, ticketEntry.embedMessageID);

        preparedStatement.executeUpdate();
        preparedStatement.close();

        disconnect();

    }

    /**
     * Retrieves a ticket entry from the database based on the provided ID and type.
     *
     * @param id   the ID to search for in the database; must correspond to the specified type (e.g., "authorID" or "threadChannelID")
     * @param type the type of ID to search by; valid values are "authorID" or "threadChannelID"
     * @return a {@code TicketEntry} object representing the ticket if found, or {@code null} if no ticket matches the criteria
     * @throws SQLException              if a database access error occurs
     * @throws IllegalArgumentException if the provided type is not "authorID" or "threadChannelID"
     */
    public TicketEntry getTicket(String id, String type) throws SQLException{
        if(!Objects.equals(type, "authorID") && !Objects.equals(type, "threadChannelID")){
            throw new IllegalArgumentException("Invalid search type. Use authorID or threadChannelID");
        }

        connect();
        String sql = "SELECT * FROM tickets WHERE " + type + " = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, id);
        ResultSet resultSet = statement.executeQuery();


        TicketEntry ticket = null;

        if(resultSet.next()) {
            ticket = TicketEntry.fromResultSet(resultSet);
        }

        resultSet.close();
        statement.close();
        disconnect();

        return ticket;

    }

    /**
     * Updates the last activity timestamp of a specific ticket in the database.
     *
     * @param ticketID The ID of the ticket to update.
     * @throws SQLException If a database access error occurs or the SQL operation fails.
     */
    public void updateTicketActivity(int ticketID) throws SQLException{
        connect();
        String sql = "UPDATE tickets SET lastActivity = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long timestampSeconds = timestamp.getTime() / 1000;
        statement.setLong(1, timestampSeconds);
        statement.setInt(2, ticketID);
        statement.executeUpdate();
        statement.close();
        disconnect();
    }

    /**
     * Deletes a ticket from the database using the specified ticket ID.
     *
     * @param ticketID The ID of the ticket to delete.
     * @throws SQLException If a database access error occurs or the delete operation fails.
     */
    public void deleteTicket(int ticketID) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM tickets WHERE id = " + ticketID);
        statement.close();
        disconnect();
    }

    /**
     * Updates the specified ticket's information in the database.
     *
     * @param ticket the TicketEntry object containing the updated information.
     * @throws SQLException if a database access error occurs.
     */
    public void updateTicket(TicketEntry ticket) throws SQLException{
        connect();
        String sql = "UPDATE tickets SET authorID = ?, threadChannelID = ?, embedMessageID = ?, lastActivity = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, ticket.authorID);
        statement.setString(2, ticket.threadChannelID);
        statement.setString(3, ticket.embedMessageID);
        statement.setLong(4, ticket.lastActive);
        statement.setInt(5, ticket.id);

        statement.executeUpdate();
        statement.close();
        disconnect();
    }

    /**
     * Retrieves a list of expired tickets from the database.
     * A ticket is considered expired if its last activity timestamp
     * is older than the current time minus the configured expiry time.
     *
     * @return a list of {@code TicketEntry} objects representing expired tickets.
     * @throws SQLException if an error occurs while interacting with the database.
     */
    public List<TicketEntry> getExpiredTickets() throws SQLException{
        long currentTime = System.currentTimeMillis();
        long timeInSeconds = currentTime / 1000;
        long expireTime = timeInSeconds - ConfigLoader.TICKET_EXPIRY_TIME_SECONDS;

        connect();
        String sql = "SELECT * FROM tickets WHERE lastActivity < ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setLong(1, expireTime);
        ResultSet resultSet = statement.executeQuery();
        List<TicketEntry> expiredTickets = new ArrayList<>();
        while (resultSet.next()){
            TicketEntry ticket = new TicketEntry(
                    resultSet.getInt("id"),
                    resultSet.getString("authorID"),
                    resultSet.getString("threadChannelID"),
                    resultSet.getString("embedMessageID"),
                    resultSet.getLong("lastActivity")
            );

            expiredTickets.add(ticket);
        }

        resultSet.close();
        statement.close();
        disconnect();

        return expiredTickets;
    }

    /**
     * Retrieves a list of all ticket entries from the database.
     *
     * This method connects to the database, retrieves all records from the "tickets" table,
     * and converts them into a list of {@link TicketEntry} objects.
     *
     * @return a list of {@link TicketEntry} objects representing all tickets in the database
     * @throws SQLException if a database access error occurs
     */
    public List<TicketEntry> getAllTickets() throws SQLException{
        connect();
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM tickets");
        List<TicketEntry> allTickets = new ArrayList<>();
        while (resultSet.next()){
            TicketEntry ticket = TicketEntry.fromResultSet(resultSet);
            allTickets.add(ticket);
        }

        resultSet.close();
        statement.close();
        disconnect();

        return allTickets;
    }
}
