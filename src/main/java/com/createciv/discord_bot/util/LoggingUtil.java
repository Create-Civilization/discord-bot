package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

/**
 * Utility class for handling logging operations, including logging whitelist events, exceptions,
 * and user actions such as joining or leaving the server. The logs are primarily sent to a
 * pre-configured Discord text channel.
 * <p>
 * This class fetches configuration values from a configuration loader, such as the log channel ID,
 * and utilizes the Discord API to send embedded messages to the appropriate guild and text channel.
 */
public class LoggingUtil {

    private static String logChannelID = ConfigLoader.LOG_CHANNEL_ID;
    private static TextChannel logChannel = Bot.API.getTextChannelById(logChannelID);
    private Guild guild = logChannel.getGuild();

    public TextChannel getLogChannel(){
        return logChannel;
    }

    /**
     * Logs a whitelist entry to the configured log channel. Creates and sends an embed message
     * with details about the whitelist entry and the interacting user.
     *
     * @param entry The whitelist entry to log, containing details such as the Minecraft username,
     *              UUID, Discord ID, reason for joining, and referral.
     * @param interactingUser The user who initiated the whitelist action.
     */
    public void logWhitelists(WhitelistEntry entry, User interactingUser){
        if(logChannel == null) { Bot.LOGGER.error("Attempted to log whitelist. No log channel found"); return;}
        String description = String.format("Minecraft Username: `%s` \n", entry.username) +
                String.format("Minecraft UUID: `%s` \n", entry.playerUUID) +
                String.format("Discord Name: <@%s> \n", entry.discordID) +
                String.format("Reason For Join: `%s` \n", entry.reason) +
                String.format("Referral: `%s`", entry.referral);

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle("New User Whitelisted")
                .setDescription(description)
                .setAuthor(interactingUser.getGlobalName(), null, interactingUser.getAvatarUrl())
                .setFooter(guild.getName() + " | " + guild.getId(), guild.getIconUrl())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }

    /**
     * Logs the removal of a whitelist entry to the configured log channel. Creates and sends an embed
     * message containing details about the removed whitelist entry and the user who initiated the action.
     *
     * @param entry The whitelist entry being removed, containing details such as the Minecraft username,
     *              UUID, Discord ID, and other metadata.
     * @param interactingUser The user who initiated the removal of the whitelist entry.
     */
    public void logRemoveWhitelist(WhitelistEntry entry, User interactingUser){
        if(logChannel == null) {Bot.LOGGER.error("Attempted to log whitelist removal. No log channel found"); return;}
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(entry.username + " has removed themself from the whitelist")
                .setAuthor(interactingUser.getGlobalName(), null, interactingUser.getAvatarUrl())
                .setFooter(guild.getName() + " | " + guild.getId(), guild.getIconUrl())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }


    /**
     * Logs an exception to the configured log channel. Creates and sends an embed message
     * containing details about the exception, including the exception message and a portion
     * of the stack trace. Logs the exception to the bot's logger as well.
     *
     * @param e The exception to log, which provides the details to include in the log message.
     */
    public static void logError(Exception e) {
        if(logChannel == null) { Bot.LOGGER.error("Attempted to log exception. No log channel found"); return;}
        StringBuilder description = new StringBuilder();
        description.append("`").append(e.toString()).append("`\n");
        for (StackTraceElement element : e.getStackTrace()) {
            description.append("`at ").append(element.toString()).append("`\n");
            if (description.length() > 1000) break;
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle("⚠️ **Exception occurred:**")
                .setDescription(description.toString())
                .setFooter(logChannel.getGuild().getName() + " | " + logChannel.getGuild().getId(), logChannel.getGuild().getIconUrl())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
        Bot.LOGGER.error("Exception occurred: {}", e.getMessage(), e);
    }


    /**
     * Logs a user joining the server by sending an embed message to the configured log channel.
     * The embed includes the user's global name, a welcome message, and footer details about the guild.
     * If no log channel is configured, an error message is logged.
     *
     * @param user The user who joined the server.
     */
    public void logUserJoin(User user){
        if(logChannel == null) {Bot.LOGGER.error("Attempted to log user join. No log channel found"); return;}

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.GREEN)
                .setTitle(user.getGlobalName() + " joined")
                .setDescription("Welcome to the server!")
                .setFooter(logChannel.getGuild().getName() + " | " + logChannel.getGuild().getId(), logChannel.getGuild().getIconUrl())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }
    public static void logUserKick(User user, String description){
        if(logChannel == null) {Bot.LOGGER.error("Attempted to log user kick. No log channel found"); return;}
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(user.getGlobalName() + " kicked")
                .setDescription(description)
                .setFooter(logChannel.getGuild().getName() + " | " + logChannel.getGuild().getId(), logChannel.getGuild().getIconUrl())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }

    /**
     * Logs the removal of a user from the server to the configured log channel. An embed message is
     * created and sent to the log channel detailing the user's departure.
     *
     * @param user The user who left the server.
     */
    public void logUserRemove(User user){
        if(logChannel == null) {Bot.LOGGER.error("Attempted to log user remove. No log channel found"); return;}

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.RED)
                .setTitle(user.getGlobalName() + " left the server")
                .setDescription("Goodbye!")
                .setFooter(logChannel.getGuild().getName() + " | " + logChannel.getGuild().getId(), logChannel.getGuild().getIconUrl())
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }

}
