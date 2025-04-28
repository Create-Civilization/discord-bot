package com.createciv.discord_bot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class ConfigLoader {
    public static final String BOT_TOKEN;

    static {
        Properties properties = new Properties();

        File configFile = new File("storage/config.properties");

        if (!configFile.exists()) {
            properties.setProperty("BOT_TOKEN", "");

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
    }
}
