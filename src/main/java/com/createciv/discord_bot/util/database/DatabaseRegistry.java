package com.createciv.discord_bot.util.database;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.managers.PunishmentTable;
import com.createciv.discord_bot.util.database.managers.TicketTable;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class DatabaseRegistry {
    private static final String dbAddress = String.format(
            "jdbc:postgresql://%s:%s/%s?sslmode=disable",
            ConfigLoader.DB_ADDRESS,
            ConfigLoader.DB_PORT,
            ConfigLoader.DB_NAME);
    private static final Map<String, TableManager<?>> managers = new HashMap<>();

    public static void init() {

        WhitelistTable whitelistTable = new WhitelistTable();
        TicketTable ticketTable = new TicketTable();
        PunishmentTable punishmentTable = new PunishmentTable();

        register("whitelist", whitelistTable);
        register("tickets", ticketTable);
        register("punishments", punishmentTable);


        for (Map.Entry<String, TableManager<?>> entry : managers.entrySet()) {
            String dbName = entry.getKey();
            TableManager<?> manager = entry.getValue();

            try {
                Bot.LOGGER.info("Initializing {} database...", dbName);
                manager.initTable();
                Bot.LOGGER.info("Initialized {} successfully", dbName);
            } catch (SQLException e) {
                LoggingUtil.error(e);
            }
        }

    }
    private static void register(String name, TableManager<?> manager) {
        managers.put(name, manager);
    }

    public static TableManager<?> getTableManager(String name){
        return managers.get(name);
    }

    public static String getDbAddress() {
        return dbAddress;
    }

    public static boolean checkDatabaseHealth() {
        boolean healthy = true;

        for (Map.Entry<String, TableManager<?>> entry : managers.entrySet()) {
            String dbName = entry.getKey();
            TableManager<?> manager = entry.getValue();

            try {
                manager.ensureConnection();
                Bot.LOGGER.debug("Checking {} database healthy...", dbName);
            } catch (SQLException e) {
                Bot.LOGGER.error("Failed to check {} database healthy", dbName, e);
                healthy = false;
            }
        }

        return healthy;
    }

}
