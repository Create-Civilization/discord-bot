package com.createciv.tests;

import com.createciv.discord_bot.util.database.types.ModerationEntry;
import com.createciv.discord_bot.util.database.types.PunishmentEntry;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Timestamp;
import java.util.List;

public class PunishmentTests {

    @Test
    public void testPunishment() {
        //Check Json to entry works

        JsonObject json = new JsonObject();
        json.addProperty("adminDiscordID", "1234567890");
        json.addProperty("punishment_type", "BAN");
        json.addProperty("reason", "Testing");
        json.addProperty("creationTimestamp", new Timestamp(System.currentTimeMillis()).getTime());
        json.addProperty("expirationTimestamp", new Timestamp(System.currentTimeMillis()).getTime());

        PunishmentEntry punishmentEntry = new PunishmentEntry(json);
        System.out.println(punishmentEntry.toString());

        if (punishmentEntry.getReason().equals("Testing")) {
            assertTrue(true);
        }


    }

    @Test
    public void testModerationEntry() {

        JsonObject json = new JsonObject();
        json.addProperty("adminDiscordID", "1234567890");
        json.addProperty("punishment_type", "BAN");
        json.addProperty("reason", "Testing");
        json.addProperty("creationTimestamp", new Timestamp(System.currentTimeMillis()).getTime());
        json.addProperty("expirationTimestamp", new Timestamp(System.currentTimeMillis()).getTime());


        JsonArray discordIDs = new JsonArray();
        discordIDs.add(1234567890);
        discordIDs.add(1234567890);

        JsonArray uuids = new JsonArray();
        uuids.add("1234567890");
        uuids.add("1234567890");

        JsonArray punishments = new JsonArray();
        punishments.add(json);
        punishments.add(json);

        ModerationEntry moderationEntry = new ModerationEntry(1, discordIDs.toString(), uuids.toString(), punishments.toString());

        List<PunishmentEntry> punishmentList = moderationEntry.getPunishments();

        for (PunishmentEntry punishment : punishmentList) {
            System.out.println(punishment.toString());
        }
    }
}
