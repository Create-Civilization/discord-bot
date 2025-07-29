package com.createciv.discord_bot.util.database;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.util.database.managers.TicketManager;
import com.createciv.discord_bot.util.database.managers.WhitelistManager;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseRegistry {
    private static final Map<String, DatabaseManager> managers = new HashMap<>();

    public static void init() {
        WhitelistManager whitelistManager = new WhitelistManager();
        TicketManager ticketManager = new TicketManager();

        register("whitelist", whitelistManager);
        register("tickets", ticketManager);

        for (Map.Entry<String, DatabaseManager> entry : managers.entrySet()) {
            String dbName = entry.getKey();
            DatabaseManager manager = entry.getValue();

            try {
                Bot.LOGGER.info("Initializing {} database...", dbName);
                manager.initDatabase();
                Bot.LOGGER.info("Initialized {} successfully", dbName);
            } catch (SQLException e) {
                Bot.LOGGER.error("Failed to initialized {}", dbName, e);
            }
        }

    }

    private static void register(String name, DatabaseManager manager) {
        managers.put(name, manager);
    }

    public static WhitelistManager getWhitelistManager() {
        return (WhitelistManager) managers.get("whitelist");
    }

    public static TicketManager getTicketManager() {
        return (TicketManager) managers.get("tickets");
    }

}
