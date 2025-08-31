package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateUsernames extends ScheduledTask {

    public UpdateUsernames() {
        super("update_usernames", TimeUnit.HOURS, 1);
    }

    @Override
    public void execute() {

        Bot.LOGGER.info("Checking for username updates");

        WhitelistManager manager = (WhitelistManager) DatabaseRegistry.getTableManager("whitelist");

        List<WhitelistEntry> whitelistEntries;

        try {
            whitelistEntries = manager.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        MojangAPI mojangAPI = new MojangAPI();
        for (WhitelistEntry entry : whitelistEntries) {
            Bot.LOGGER.info("Updating username for {}", entry.username);
            JsonObject playerData = mojangAPI.getPlayerInfo(entry.playerUUID.toString());
            if (playerData == null) {
                throw new RuntimeException("Failed to update username for " + entry.username);
            }
            if (playerData.get("username") != null && !playerData.get("username").getAsString().equals(entry.username)) {
                Bot.LOGGER.info("Username changed for {} updating to {}", entry.username, playerData.get("username").getAsString());
                entry.username = playerData.get("username").getAsString();
                try {
                    manager.update(entry);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return;
            }
            Bot.LOGGER.info("Username is up to date for {}", entry.username);
        }

        Bot.LOGGER.info("Finished updating usernames");

    }
}
