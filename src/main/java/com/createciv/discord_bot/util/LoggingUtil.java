package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.awt.*;
import java.util.Arrays;
import java.util.stream.Collectors;

public class LoggingUtil {

	public static TextChannel getLogChannel() {
		return Bot.API.getTextChannelById(ConfigLoader.LOG_CHANNEL_ID);
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

		getLogChannel().sendMessageEmbeds(errorEmbed).queue();
	}

	public static void log(Color color, String title, String message) {
		MessageEmbed embed = new EmbedBuilder()
			.setTitle(title)
			.setColor(color)
			.setDescription(message)
			.build();

		getLogChannel().sendMessageEmbeds(embed).queue();
	}
}