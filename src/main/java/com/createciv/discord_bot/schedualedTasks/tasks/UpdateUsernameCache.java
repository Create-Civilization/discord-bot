package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.UsernameCacheTable;
import com.createciv.discord_bot.util.database.types.UsernameCacheEntry;
import com.google.gson.JsonObject;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.createciv.discord_bot.Bot.LOGGER;

public class UpdateUsernameCache extends ScheduledTask {

    public UpdateUsernameCache() {
        super("UpdateUsernameCache", TimeUnit.SECONDS, 10);
    }

    @Override
    public void execute() {
        UsernameCacheTable table = (UsernameCacheTable) DatabaseRegistry.getTableManager("usernameCache");
        try {
            List<UsernameCacheEntry> cacheEntries = table.getExpired();
            for (UsernameCacheEntry entry : cacheEntries) {
                JsonObject response = MojangAPI.getPlayerInfo(entry.playerUUID.toString());
                if (response == null) {
                    LOGGER.warn("Error getting player info for " + entry.playerUUID.toString());
                    continue;
                }
                //Check if we got an invalid username
                if (response.get("reason") != null) {
                    LOGGER.warn(response.get("reason").toString());
                    continue;
                }

                String username = response.get("username").getAsString();
                if(username.equals(entry.username)){
                    LOGGER.warn("No updated needed for " +  entry.playerUUID.toString());
                    continue;
                }
                LOGGER.info("Updated Username For Player");
                table.updateUsername(entry.playerUUID, username);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
