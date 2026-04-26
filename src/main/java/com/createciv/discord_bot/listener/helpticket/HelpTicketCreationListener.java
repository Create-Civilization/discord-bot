package com.createciv.discord_bot.listener.helpticket;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.TicketTable;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class HelpTicketCreationListener extends ListenerAdapter {
	@Override
	public void onMessageReceived(MessageReceivedEvent msg){
		if (msg.isFromType(ChannelType.PRIVATE)){
			String authorID = msg.getAuthor().getId();
			TicketTable manager = (TicketTable) DatabaseRegistry.getTableManager("tickets");
			try {
				TicketEntry ticket = manager.get(authorID);
				if (ticket != null){ //handle existing ticket
					JDA jda = msg.getJDA();
					Guild guild = jda.getGuildById(ConfigLoader.GUILD_ID);
					if (guild ==null) {return;}
					ThreadChannel threadChannel = guild.getThreadChannelById(ticket.threadChannelID);
					//threadChannel.sendMessageEmbeds()
				}
				else { //create ticket - start by making embed msg and thread
					JDA jda = msg.getJDA();
					User sender = msg.getAuthor();
					String senderID = sender.getId();
					String username = sender.getName();
					Guild guild = jda.getGuildById(ConfigLoader.GUILD_ID);
					if (guild == null) {return;}
					MessageEmbed startingEmbed = new EmbedBuilder()
						.setTitle("Help Ticket for " + username)
						.setDescription("open ticket")
						.setColor(Color.decode("#809ae8"))
						.setFooter(username + " | " + senderID, sender.getAvatarUrl())
						.build();
					TextChannel helpTicketChannel = guild.getTextChannelById(ConfigLoader.HELP_TICKET_CHANNEL_ID);
					if (helpTicketChannel == null) {return;}
					Message starter = helpTicketChannel.sendMessageEmbeds(startingEmbed).complete();
					ThreadChannel thread = helpTicketChannel.createThreadChannel("threadchan",starter.getId()).complete();
					String threadID = thread.getId();
					TicketEntry ticketToAdd = new TicketEntry.Builder()
						.authorID(senderID)
						.embedMessageID(starter.getId())
						.threadChannelID(threadID)
						.lastActivity(Timestamp.from(Instant.now()))
						.build();
					try {
						manager.add(ticketToAdd);
					} catch (SQLException e){
						throw new SQLException(e);
					}
				}
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
