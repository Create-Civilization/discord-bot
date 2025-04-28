package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.DatabaseEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;

public class TicketEntry extends DatabaseEntry {

    public int id = 0;
    public String authorID;
    public String threadChannelID;
    public String embedMessageID;
    public long lastActive;


    public TicketEntry(String authorID, String threadChannelID, String embedMessageID){
        this.authorID = authorID;
        this.threadChannelID = threadChannelID;
        this.embedMessageID = embedMessageID;
        this.lastActive = Instant.now().getEpochSecond();
    }

    public TicketEntry(int id, String authorID, String threadChannelID, String embedMessageID, long lastActivity) {
        this.id = id;
        this.authorID = authorID;
        this.threadChannelID = threadChannelID;
        this.embedMessageID = embedMessageID;
        this.lastActive = lastActivity;
    }

    public static TicketEntry fromResultSet(ResultSet resultSet) throws SQLException {
        return new TicketEntry(resultSet.getInt("id"), resultSet.getString("authorID"), resultSet.getString("threadChannelID"), resultSet.getString("embedMessageID"), resultSet.getInt("lastActivity"));
    }
}
