package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;

public class WhitelistEntry extends TableEntry {

    private String playerUUID;
    private String discordID;


    public WhitelistEntry(String playerUUID, String discordID) {
        this.playerUUID = playerUUID;
        this.discordID = discordID;
    }

    public String getPlayerUUID() {
        return playerUUID;
    }
    public String getDiscordID() {
        return discordID;
    }


    @Override
    public TableEntry fromResultSet(ResultSet resultSet) throws SQLException {
        return null;
    }
}
