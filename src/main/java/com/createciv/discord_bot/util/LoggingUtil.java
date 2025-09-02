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

}
