package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class TicketEntry extends TableEntry<TicketEntry> {
    public int id;
    public String authorID;
    public String threadChannelID;
    public String embedMessageID;
    public Timestamp lastActivity;



    public TicketEntry(ResultSet resultSet) throws SQLException {
        id = resultSet.getInt("id");
        authorID = resultSet.getString("authorID");
        threadChannelID = resultSet.getString("threadChannelID");
        embedMessageID = resultSet.getString("embedMessageID");
        lastActivity = resultSet.getTimestamp("lastActivity");
    }
}
