package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.util.database.DatabaseRegistry;

import java.util.concurrent.TimeUnit;

public class DatabaseConnection extends ScheduledTask {

    public DatabaseConnection(String name, TimeUnit timeUnit, int interval) {
        super("database_connection", TimeUnit.MINUTES, 1);
    }

    @Override
    public void execute() throws Exception {
        boolean prior = Bot.DB_HEALTHY;
        Bot.DB_HEALTHY = DatabaseRegistry.checkDatabaseHealth();

        if (prior && !Bot.DB_HEALTHY) {
            Bot.LOGGER.error("Database connection lost - all reconnection attempts failed");
        } else if (!prior && Bot.DB_HEALTHY) {
            Bot.LOGGER.info("Database connection restored");
        } else if (!Bot.DB_HEALTHY) {
            Bot.LOGGER.warn("Database health check failed - attempted reconnection");
        }
    }
}
