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
import java.util.Arrays;
import java.util.stream.Collectors;

import static com.createciv.discord_bot.Bot.LOGGER;


public class LoggingUtil {

    private static final String logChannelID = ConfigLoader.LOG_CHANNEL_ID;
    private static final TextChannel logChannel = Bot.API.getTextChannelById(logChannelID);
    private static final Guild guild = logChannel.getGuild();

    public static TextChannel getLogChannel(){
        return logChannel;
    }

    public static void error(Exception e) {

        StackTraceElement[] stackTrace = e.getStackTrace();

        String traceString = Arrays.stream(stackTrace)
                .limit(5)
                .map(StackTraceElement::toString)
                .collect(Collectors.joining("\n"));

        MessageEmbed errorEmbed = new EmbedBuilder()
                .setTitle(e.getMessage())
                .setColor(Color.red)
                .addField("Stack Trace", "```" + traceString + "..." + "```", true)
                .build();

        logChannel.sendMessageEmbeds(errorEmbed).queue();
    }

    public static void log(Color color, String title, String message){
        MessageEmbed embed = new EmbedBuilder()
                .setTitle(title)
                .setColor(color)
                .setDescription(message)
                .build();

        logChannel.sendMessageEmbeds(embed).queue();
    }

}
