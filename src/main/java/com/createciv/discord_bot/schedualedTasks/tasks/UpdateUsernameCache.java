package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.UsernameCacheTable;
import com.createciv.discord_bot.util.database.types.UsernameCacheEntry;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class UpdateUsernameCache extends ScheduledTask {

    public UpdateUsernameCache(String name, TimeUnit timeUnit, int interval) {
        super(name, timeUnit, interval);
    }

    @Override
    public void execute() {
        UsernameCacheTable table = (UsernameCacheTable) DatabaseRegistry.getTableManager("usernameCache");
        try {
            List<UsernameCacheEntry> cacheEntries = table.getExpired();
            if (cacheEntries != null) {
                for (UsernameCacheEntry usernameCacheEntry : cacheEntries) {
                   // table.remove(usernameCacheEntry.playerUUID);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
