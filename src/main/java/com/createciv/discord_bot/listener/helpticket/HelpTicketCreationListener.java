package com.createciv.discord_bot.listener.helpticket;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.EmbedUtil;
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
			Timestamp time = Timestamp.from(Instant.now());
			JDA jda = msg.getJDA();
			Guild guild = jda.getGuildById(ConfigLoader.GUILD_ID);
			String mssg = msg.getMessage().getContentRaw();
			if (guild ==null) {return;}
			User sender = msg.getAuthor();
			String authorID = msg.getAuthor().getId();
			TicketTable manager = (TicketTable) DatabaseRegistry.getTableManager("tickets");
			try {
				TicketEntry ticket = manager.get(authorID);
				if (ticket != null){ //handle existing ticket
					ThreadChannel threadChannel = guild.getThreadChannelById(ticket.threadChannelID);
					MessageEmbed messageToSend = EmbedUtil.InternalTextTicketMessage(sender,mssg,"Message Received",time);
					assert threadChannel != null;
					threadChannel.sendMessageEmbeds(messageToSend).complete();
				}
				else { //create ticket - start by making embed msg and thread
					MessageEmbed startingEmbed = EmbedUtil.StarterTicketChannelEmbed(sender);
					TextChannel helpTicketChannel = guild.getTextChannelById(ConfigLoader.HELP_TICKET_CHANNEL_ID);
					if (helpTicketChannel == null) {return;}
					Message starter = helpTicketChannel.sendMessageEmbeds(startingEmbed).complete();
					ThreadChannel thread = helpTicketChannel.createThreadChannel("threadchan",starter.getId()).complete();
					TicketEntry ticketToAdd = new TicketEntry.Builder()
						.authorID(authorID)
						.embedMessageID(starter.getId())
						.threadChannelID(thread.getId())
						.lastActivity(Timestamp.from(Instant.now()))
						.build();
					try {
						manager.add(ticketToAdd);
					} catch (SQLException e){
						throw new SQLException(e);
					}
					thread.sendMessageEmbeds(EmbedUtil.BasicEmbed("New Ticket Has Been Opened","To respond to this ticket use /reply every other message will be ignored. To close the ticket do /close this ticket will automatically close after 7 days",Color.gray)).complete();
					thread.sendMessageEmbeds(EmbedUtil.InternalTextTicketMessage(sender,mssg,"Message Received",time)).complete();
				}
			}
			catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}
}
