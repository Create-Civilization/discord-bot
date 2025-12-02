package com.createciv.discord_bot.util.database.managers;

import com.createciv.discord_bot.util.database.TableManager;
import com.createciv.discord_bot.util.database.types.PunishmentEntry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

public class PunishmentTable extends TableManager<PunishmentEntry> {
    @Override
    public void initTable() throws SQLException {
        connect();
        Statement statement = connection.createStatement();
        statement.execute(
                "CREATE TABLE IF NOT EXISTS punishments (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "discordID TEXT," +
                        "playerUUID TEXT," +
                        "punishmentReason TEXT NOT NULL, " +
                        "punishmentType INTEGER NOT NULL, " + //0 Mute //1 Kick //2 TempBan
                        "punishmentExpiration INTEGER NOT NULL, " +
                        "adminID TEXT NOT NULL, " +
                        "punishmentLocation TEXT NOT NULL) "
        );
        statement.close();
        disconnect();
    }

    @Override
    public void add(PunishmentEntry tableEntry) throws SQLException {
        connect();
        PreparedStatement statement = connection.prepareStatement("INSERT INTO punishments(id,discordID,playerUUID,punishmentReason,punishmentType,punishmentExpiration,adminID,punishmentLocation) VALUES (?,?,?,?,?,?,?,?,?)");

    }
}
