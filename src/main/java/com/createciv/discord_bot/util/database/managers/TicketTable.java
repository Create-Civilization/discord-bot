package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.TableManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class TicketTable extends TableManager<TicketEntry> {

    @Override
    public void initTable() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS tickets (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "authorID TEXT NOT NULL," +
                        "threadChannelID TEXT NOT NULL," +
                        "embedMessageID TEXT NOT NULL," +
                        "lastActivity INTEGER DEFAULT (strftime('%s', 'now')))"
        );
        statement.close();
        disconnect();
    }

    @Override
    public void add(TicketEntry tableEntry) throws SQLException {
        connect();
        PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO tickets (authorID, threadChannelID, embedMessageID, lastActivity) VALUES (?, ?, ?, ?)");
        preparedStatement.setString(1, tableEntry.authorID);
        preparedStatement.setString(2, tableEntry.threadChannelID);
        preparedStatement.setString(3, tableEntry.embedMessageID);
        if(tableEntry.lastActivity != null){
            preparedStatement.setTimestamp(4, tableEntry.lastActivity);
        }
        preparedStatement.execute();
        preparedStatement.close();
        disconnect();
    }
}
