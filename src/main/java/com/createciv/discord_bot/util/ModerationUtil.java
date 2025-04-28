package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import net.dv8tion.jda.api.entities.EmbedType;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.time.OffsetDateTime;
import java.util.Objects;

import static com.createciv.discord_bot.ConfigLoader.GUILD_ID;
import static com.createciv.discord_bot.ConfigLoader.LOG_CHANNEL_ID;

public class ModerationUtil {

    public static void log(LogEmbed logEmbed) {
        Objects.requireNonNull(Bot.API.getChannelById(TextChannel.class, LOG_CHANNEL_ID)).sendMessageEmbeds(logEmbed).queue();
    }

    public static class LogEmbed extends MessageEmbed {
        public LogEmbed(String title, String description, boolean goodOrBad) {
            super(null, title, description, EmbedType.RICH, OffsetDateTime.now(), goodOrBad ? Color.GREEN.getRGB() : Color.RED.getRGB(), null, null, (new MessageEmbed.AuthorInfo(Bot.BOT.getName(), null, null, null)), null, (new MessageEmbed.Footer(Objects.requireNonNull(Bot.API.getGuildById(GUILD_ID)).getName(), null, null)), null, null);
        }
    }
}
