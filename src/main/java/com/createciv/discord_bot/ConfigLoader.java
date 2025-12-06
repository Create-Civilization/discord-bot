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

	// Env Vars
	public static final String BOT_TOKEN;
	public static final String PANEL_TOKEN;
	public static final String DB_ADDRESS;
	public static final String DB_PORT;
	public static final String DB_NAME;
	public static final String DB_USER;
	public static final String DB_PASSWORD;

	// Discord
	public static final String GUILD_ID;
	public static final String LOG_CHANNEL_ID;
	public static final String HELP_TICKET_CHANNEL_ID;
	public static final String WHITELIST_ROLE_ID;
	public static final String BANNED_ROLE_ID;
	public static final List<String> ADMIN_ROLE_IDS;

	// Minecraft Server
	public static final String SERVER_ID;
	public static final String SERVER_IP;
	public static final String SERVER_PORT;
	public static final String PANEL_URL;

	// Other
	public static final long TICKET_EXPIRY_TIME_SECONDS;

	static {
		BOT_TOKEN = getEnv("BOT_TOKEN");
		PANEL_TOKEN = getEnv("PANEL_TOKEN");
		DB_ADDRESS = getEnv("DB_ADDRESS");
		DB_PORT = getEnv("DB_PORT");
		DB_NAME = getEnv("DB_NAME");
		DB_USER = getEnv("DB_USER");
		DB_PASSWORD = getEnv("DB_PASSWORD");

		Properties properties = loadOrCreateConfig();

		GUILD_ID = properties.getProperty("GUILD_ID");
		LOG_CHANNEL_ID = properties.getProperty("LOG_CHANNEL_ID");
		HELP_TICKET_CHANNEL_ID = properties.getProperty("HELP_TICKET_CHANNEL_ID");
		WHITELIST_ROLE_ID = properties.getProperty("WHITELIST_ROLE_ID");
		BANNED_ROLE_ID = properties.getProperty("BANNED_ROLE_ID");

		String adminRolesString = properties.getProperty("ADMIN_ROLE_IDS", "");
		ADMIN_ROLE_IDS = adminRolesString.isEmpty() ? new ArrayList<>() : Arrays.asList(adminRolesString.split(","));

		SERVER_ID = properties.getProperty("SERVER_ID");
		SERVER_IP = properties.getProperty("SERVER_IP");
		SERVER_PORT = properties.getProperty("SERVER_PORT");
		PANEL_URL = properties.getProperty("PANEL_URL");

		TICKET_EXPIRY_TIME_SECONDS = Long.parseLong(
			properties.getProperty("TICKET_EXPIRY_TIME_SECONDS", "604800")
		);
	}

	private static Properties loadOrCreateConfig() {
		Properties properties = new Properties();
		File configFile = new File("config.properties");

		if (!configFile.exists()) createDefaultConfig(configFile, properties);

		try (FileInputStream fis = new FileInputStream(configFile)) {
			properties.load(fis);
			Bot.LOGGER.info("Config successfully loaded from {}", configFile.getAbsolutePath());
		} catch (IOException e) {
			Bot.LOGGER.error("Error loading config file", e);
			throw new RuntimeException("Error loading config file", e);
		}

		return properties;
	}

	private static void createDefaultConfig(File configFile, Properties properties) {
		properties.setProperty("GUILD_ID", "");
		properties.setProperty("LOG_CHANNEL_ID", "");
		properties.setProperty("HELP_TICKET_CHANNEL_ID", "");
		properties.setProperty("WHITELIST_ROLE_ID", "");
		properties.setProperty("BANNED_ROLE_ID", "");
		properties.setProperty("ADMIN_ROLE_IDS", "");
		properties.setProperty("SERVER_ID", "");
		properties.setProperty("SERVER_IP", "");
		properties.setProperty("SERVER_PORT", "");
		properties.setProperty("PANEL_URL", "");
		properties.setProperty("TICKET_EXPIRY_TIME_SECONDS", "604800");

		try (FileOutputStream fos = new FileOutputStream(configFile)) {
			properties.store(fos, "Bot Configuration - Fill in all values");
			Bot.LOGGER.info("Default config created at {}", configFile.getAbsolutePath());
		} catch (IOException e) {
			Bot.LOGGER.error("Failed to create default config", e);
			throw new RuntimeException("Failed to create default config", e);
		}
	}

	private static String getEnv(String key) {
		String value = System.getenv(key);
		if (value == null || value.isEmpty()) {
			Bot.LOGGER.error("Missing environment variable: {}", key);
			throw new IllegalStateException("Missing environment variable: " + key);
		}
		return value;
	}

	private static String getEnvOrDefault(String key, String defaultValue) {
		String value = System.getenv(key);
		return (value == null || value.isEmpty()) ? defaultValue : value;
	}
}