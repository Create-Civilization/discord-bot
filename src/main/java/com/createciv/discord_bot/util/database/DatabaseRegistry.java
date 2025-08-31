package com.createciv.discord_bot.util.database;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.util.database.managers.TicketManager;
import com.createciv.discord_bot.util.database.managers.WhitelistManager;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseRegistry {
    private static final Map<String, TableManager> managers = new HashMap<>();

    private static String dbPath;

    public static void init() {

        File dir = new File("storage");
        if(!dir.exists()){
            dir.mkdirs();
        }

        dbPath = "jdbc:sqlite:storage/createcivilization.db";


        WhitelistManager whitelistManager = new WhitelistManager();
        TicketManager ticketManager = new TicketManager();

        register("whitelist", whitelistManager);
        register("tickets", ticketManager);

        for (Map.Entry<String, TableManager> entry : managers.entrySet()) {
            String dbName = entry.getKey();
            TableManager manager = entry.getValue();

            try {
                Bot.LOGGER.info("Initializing {} database...", dbName);
                manager.initDatabase();
                Bot.LOGGER.info("Initialized {} successfully", dbName);
            } catch (SQLException e) {
                Bot.LOGGER.error("Failed to initialized {}", dbName, e);
            }
        }

    }

    private static void register(String name, TableManager manager) {
        managers.put(name, manager);
    }

    public  static TableManager getTableManager(String name){
        return managers.get(name);
    }

}
