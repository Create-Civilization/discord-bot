package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.database.DatabaseManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TicketManager extends DatabaseManager {
    public TicketManager() {
        super("tickets");
    }

    @Override
    public void initDatabase() throws SQLException {
        connect();
        Statement statement = connection.createStatement();

        statement.execute("CREATE TABLE IF NOT EXISTS tickets (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "authorID TEXT NOT NULL, " +
                "threadChannelID TEXT NOT NULL, " +
                "embedMessageID TEXT NOT NULL, " +
                "lastActivity INTEGER DEFAULT (strftime('%s', 'now'))" +
                ")");

        statement.close();
        disconnect();

    }

    /**
     *
     * @param authorID Ticket creators discord ID
     * @param threadID ID of Tickets Thread
     * @param embedMessageID embedMessageUD
     */
    public void createTicket(String authorID, String threadID, String embedMessageID) throws SQLException{
        connect();

        String sql = "INSERT INTO tickets (authorID, threadChannelID, embedMessageID) VALUES (?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);

        preparedStatement.setString(1, authorID);
        preparedStatement.setString(2, threadID);
        preparedStatement.setString(3, embedMessageID);

        preparedStatement.executeUpdate();
        preparedStatement.close();

        disconnect();

    }

    /**
     *
     * @param id id of either author or channel
     * @param type what to search with either authorID or threadChannelID
     * @return Ticket
     * @throws SQLException
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
            int ID = resultSet.getInt("id");
            String authorID = resultSet.getString("authorID");
            String threadChannelID = resultSet.getString("threadChannelID");
            String embedMessageID = resultSet.getString("embedMessageID");
            int lastActivity = resultSet.getInt("lastActivity");

            ticket = new TicketEntry(ID, authorID, threadChannelID, embedMessageID, lastActivity);
        }

        resultSet.close();
        statement.close();
        disconnect();

        return ticket;

    }

    /**
     * Deletes ticket by the ticketsID
     * @param ticketID
     * @throws SQLException
     */
    public void deleteTicket(int ticketID) throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM tickets WHERE id = " + ticketID);
        statement.close();
        disconnect();
    }

    /**
     * Update a ticket in the database.
     * @param ticket
     * @throws SQLException
     */
    public void updateTicket(TicketEntry ticket) throws SQLException{
        connect();
        String sql = "UPDATE tickets SET authorID = ?, threadChannelID = ?, embedMessageID = ?, lastActivity = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, ticket.getAuthorID());
        statement.setString(2, ticket.getThreadChannelID());
        statement.setString(3, ticket.getEmbedMessageID());
        statement.setLong(4, ticket.getLastActive());
        statement.setInt(5, ticket.getID());

        statement.executeUpdate();
        statement.close();
        disconnect();
    }

    /**
     * Returns a list of expired tickets
     * @return
     * @throws SQLException
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
                    resultSet.getLong("lastActivity"));

            expiredTickets.add(ticket);
        }

        resultSet.close();
        statement.close();
        disconnect();

        return expiredTickets;
    }
}
