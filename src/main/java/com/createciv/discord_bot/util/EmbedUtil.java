package com.createciv.discord_bot.util;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;

public class EmbedUtil {
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
	public static MessageEmbed InternalTextTicketMessage(User sender, String message, String title){
		String senderID = sender.getId();
		String username = sender.getName();
		MessageEmbed embed = new EmbedBuilder()
			.setAuthor(username,"https://discord.com/users/" + senderID,sender.getAvatarUrl())
			.setTitle(title)
			.setDescription(message)
			.build();
		return embed;
	}
}
