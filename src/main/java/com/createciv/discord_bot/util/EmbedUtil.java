package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;

public class EmbedUtil {
	public static MessageEmbed BasicEmbed (String title, String description, Color color){
		User bot = Bot.BOT;
		Timestamp timesent = Timestamp.from(Instant.now());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String readable = sdf.format(timesent);
		MessageEmbed embed = new EmbedBuilder()
			.setTitle(title)
			.setDescription(description)
			.setColor(color)
			.setFooter(bot.getName() + " on " + readable, bot.getAvatarUrl())
			.build();
		return embed;
	}
	//TODO make it so it takes optional input of link to help ticket thread
	public static MessageEmbed StarterTicketChannelEmbed(User sender){
		String senderID = sender.getId();
		String username = sender.getName();
		MessageEmbed starter = new EmbedBuilder()
			.setTitle("Help Ticket for " + username)
			.setDescription("open ticket")
			.setColor(Color.decode("#8CC084"))
			.setFooter(username + " | " + senderID, sender.getAvatarUrl())
			.build();
		return starter;
	}
	public static MessageEmbed InternalTextTicketMessage(User sender, String message, String title, Timestamp timesent){
		User bot = Bot.BOT;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String readable = sdf.format(timesent);
		String senderID = sender.getId();
		String username = sender.getName();
		MessageEmbed embed = new EmbedBuilder()
			.setAuthor(username,"https://discord.com/users/" + senderID,sender.getAvatarUrl())
			.setTitle(title)
			.setColor(Color.decode("#8CC084"))
			.setDescription(message)
			.setFooter(bot.getName() + " on " + readable, bot.getAvatarUrl())
			.build();
		return embed;
	}
}
