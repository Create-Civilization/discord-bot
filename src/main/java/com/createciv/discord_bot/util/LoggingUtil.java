package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;

public class LoggingUtil {

    private String logChannelID = ConfigLoader.LOG_CHANNEL_ID;
    private TextChannel logChannel = Bot.API.getTextChannelById(logChannelID);
    private Guild guild = logChannel.getGuild();

    public TextChannel getLogChannel(){
        return logChannel;
    }

    /**
     * Logs whitelist entries
     *
     * @param entry whitelist info to be logged
     * @param interactingUser the user who created the whitelist entry
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
     * Logs an exception to the configured log channel.
     *
     * @param e the exception to be logged
     */
    public void logError(Exception e){
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
