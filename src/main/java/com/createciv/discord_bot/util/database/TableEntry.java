package com.createciv.discord_bot.util.database;

import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class TableEntry {

    public abstract TableEntry fromResultSet(ResultSet resultSet) throws SQLException;

}
