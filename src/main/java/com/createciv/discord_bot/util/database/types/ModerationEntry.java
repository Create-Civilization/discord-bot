package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.DatabaseEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ModerationEntry extends DatabaseEntry {
    public int id;
    public JsonArray discordIDs;
    public JsonArray uuids;
    public List<PunishmentEntry> activePunishments;
    public List<PunishmentEntry> expiredPunishments;

    public ModerationEntry(int id, String discordIDs, String uuids, String activePunishments, String expiredPunishments) {
        //Make DiscordIDs into JSON
        JsonArray discordIDsArray = JsonParser.parseString(discordIDs).getAsJsonArray();
        //Make uuids string to JSON
        JsonArray uuidsArray = JsonParser.parseString(uuids).getAsJsonArray();
        //Make it an array, so I can loop over it.
        JsonArray punishmentArray = JsonParser.parseString(activePunishments).getAsJsonArray();

        JsonArray expiredPunishmentArray = JsonParser.parseString(expiredPunishments).getAsJsonArray();

        List<PunishmentEntry> activePunishmentList = new ArrayList<>();

        for (JsonElement punishmentElement : punishmentArray) {
            JsonObject punishmentObject = punishmentElement.getAsJsonObject();
            activePunishmentList.add(new PunishmentEntry(punishmentObject));
        }

        List<PunishmentEntry> expiredPunishmentList = new ArrayList<>();
        for (JsonElement punishmentElement : expiredPunishmentArray) {
            JsonObject punishmentObject = punishmentElement.getAsJsonObject();
            expiredPunishmentList.add(new PunishmentEntry(punishmentObject));
        }

        this.id = id;
        this.discordIDs = discordIDsArray;
        this.uuids = uuidsArray;
        this.activePunishments = activePunishmentList;
        this.expiredPunishments = expiredPunishmentList;
    }

    public static ModerationEntry fromResultSet(ResultSet resultSet) throws SQLException {

        return new ModerationEntry(
                resultSet.getInt("id"),
                resultSet.getString("discordIDs"),
                resultSet.getString("uuids"),
                resultSet.getString("activePunishments"),
                resultSet.getString("expiredPunishments")
        );
    }

    public void addPunishment(PunishmentEntry punishmentEntry) {
        activePunishments.add(punishmentEntry);
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

    public List<PunishmentEntry> getActivePunishments() {
        return activePunishments;
    }

    public List<PunishmentEntry> getExpiredPunishments() {
        return expiredPunishments;
    }
}
