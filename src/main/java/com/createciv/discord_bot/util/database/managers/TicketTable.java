package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.TableEntry;
import com.createciv.discord_bot.util.database.TableManager;

import java.sql.SQLException;
import java.sql.Statement;

public class TicketTable extends TableManager {

    @Override
    public void initTable() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
    }

    @Override
    public void add(TableEntry databaseEntry) throws SQLException {

    }
}
