package com.createciv.discord_bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigLoader {
    public static final String BOT_TOKEN;
    public static final String CLIENT_ID;
    public static final String GUILD_ID;
    public static final String HELP_TICKET_CHANNEL_ID;
    public static final String WHITELIST_ROLE_ID;
    public static final String SERVER_ID;
    public static final String SERVER_IP;
    public static final String SERVER_PORT;
    public static final String LOG_CHANNEL_ID;
    public static final int TICKET_EXPIRY_TIME_SECONDS;
    public static final String PETRO_PANEL_TOKEN;
    public static final List<String> ADMIN_ROLE_IDS;

    static {
        Properties properties = new Properties();

        File configFile = new File("storage/config.properties");

        if (!configFile.exists()) {

            //Setup Config.
            //Bot Token
            properties.setProperty("BOT_TOKEN", "");
            properties.setProperty("CLIENT_ID", "");
            properties.setProperty("GUILD_ID", "");
            properties.setProperty("PETRO_PANEL_TOKEN", "");
            properties.setProperty("SERVER_ID", "");
            properties.setProperty("SERVER_IP", "");
            properties.setProperty("SERVER_PORT", "");
            properties.setProperty("LOG_CHANNEL_ID", "");
            properties.setProperty("HELP_TICKET_CHANNEL_ID", "");
            properties.setProperty("WHITELIST_ROLE_ID", "");
            properties.setProperty("TICKET_EXPIRY_TIME_SECONDS", "");
            properties.setProperty("ADMIN_ROLE_IDS", "");



            configFile.getParentFile().mkdirs();
            try (FileOutputStream fos = new FileOutputStream(configFile)) {
                properties.store(fos, "Default configuration");
                Bot.LOGGER.info("Default config created at {}", configFile.getAbsolutePath());
            } catch (IOException e) {
                Bot.LOGGER.error("Failed to create default config", e);
                throw new RuntimeException("Failed to create default config", e);
            }
        }

        try (FileInputStream fis = new FileInputStream(configFile)) {
            properties.load(fis);
            Bot.LOGGER.info("Config successfully loaded from {}", configFile.getAbsolutePath());
        } catch (IOException e) {
            Bot.LOGGER.error("Error loading config file",e);
            throw new RuntimeException("Error loading config file", e);
        }



        BOT_TOKEN = properties.getProperty("BOT_TOKEN");
        CLIENT_ID = properties.getProperty("CLIENT_ID");
        GUILD_ID = properties.getProperty("GUILD_ID");
        PETRO_PANEL_TOKEN = properties.getProperty("PETRO_PANEL_TOKEN");
        SERVER_ID = properties.getProperty("SERVER_ID");
        SERVER_IP = properties.getProperty("SERVER_IP");
        SERVER_PORT = properties.getProperty("SERVER_PORT");
        LOG_CHANNEL_ID = properties.getProperty("LOG_CHANNEL_ID");
        HELP_TICKET_CHANNEL_ID = properties.getProperty("HELP_TICKET_CHANNEL_ID");
        WHITELIST_ROLE_ID = properties.getProperty("WHITELIST_ROLE_ID");
        TICKET_EXPIRY_TIME_SECONDS = Integer.parseInt(properties.getProperty("TICKET_EXPIRY_TIME_SECONDS", "604800"));

        String adminRolesString = properties.getProperty("ADMIN_ROLE_IDS", "");
        if (adminRolesString.isEmpty()) {
            ADMIN_ROLE_IDS = new ArrayList<>();
        } else {
            ADMIN_ROLE_IDS = Arrays.asList(adminRolesString.split(","));
        }
    }
}
