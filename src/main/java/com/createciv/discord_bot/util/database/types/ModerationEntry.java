package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModerationEntry extends DatabaseEntry {
    public int id;
    public JsonArray discordIDs;
    public JsonArray uuids;
    public List<PunishmentEntry> punishments;

    public ModerationEntry(int id, String discordIDs, String uuids, String punishments) {
        //Make DiscordIDs into JSON
        JsonArray discordIDsArray = JsonParser.parseString(discordIDs).getAsJsonArray();
        //Make uuids string to JSON
        JsonArray uuidsArray = JsonParser.parseString(uuids).getAsJsonArray();
        //Make it an array, so I can loop over it.
        JsonArray punishmentArray = JsonParser.parseString(punishments).getAsJsonArray();

        List<PunishmentEntry> punishmentList = new ArrayList<>();

        for (JsonElement punishmentElement : punishmentArray) {
            JsonObject punishmentObject = punishmentElement.getAsJsonObject();
            punishmentList.add(new PunishmentEntry(punishmentObject));
        }

        this.id = id;
        this.discordIDs = discordIDsArray;
        this.uuids = uuidsArray;
        this.punishments = punishmentList;
    }

    public static ModerationEntry fromResultSet(ResultSet resultSet) throws SQLException {

        return new ModerationEntry(
                resultSet.getInt("id"),
                resultSet.getString("discordIDs"),
                resultSet.getString("uuids"),
                resultSet.getString("punishments")
        );
    }

    //Geters
    public int getId() {
        return id;
    }

    public JsonArray getDiscordIDs() {
        return discordIDs;
    }

    public JsonArray getUuids() {
        return uuids;
    }

    public List<PunishmentEntry> getPunishments() {
        return punishments;
    }
}
