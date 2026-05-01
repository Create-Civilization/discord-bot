package com.createciv.discord_bot.commands.helpticketcmds;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.EmbedUtil;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.TicketTable;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.UserSnowflake;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class ReplyToTicket extends SlashCommand {
	//TODO make anon reply option
	public ReplyToTicket() {
		super("reply","reply to a ticket");
		addOption(new Option(OptionType.STRING,"response","type your response here",true, false));
	}
	@Override
	public void execute(SlashCommandInteractionEvent interactionEvent) {
		JDA jda = interactionEvent.getJDA();
		User sender = interactionEvent.getUser();
		if (!interactionEvent.getChannel().getType().isThread()){
			interactionEvent.reply("Enter a valid thread").setEphemeral(true).queue();}
		ThreadChannel threadChannel = interactionEvent.getChannel().asThreadChannel();
		TicketTable manager = (TicketTable) DatabaseRegistry.getTableManager("tickets");
		TicketEntry ticket = null;
		try {
			ticket = manager.getFromThreadID(threadChannel.getId());
		} catch (SQLException e) {
			LoggingUtil.error(e);
		}
		if (ticket != null){
			String ticketMakerID = ticket.getAuthorID();
			MessageEmbed messageEmbedToSend = EmbedUtil.InternalTextTicketMessage(sender,interactionEvent.getOption("response").getAsString(),"Message Received", Timestamp.from(Instant.now()),"#8CC084");
			MessageEmbed embedToSendToThread = EmbedUtil.InternalTextTicketMessage(sender,interactionEvent.getOption("response").getAsString(),"Message Sent", Timestamp.from(Instant.now()),"#8CC084");
			jda.retrieveUserById(ticketMakerID)
				.flatMap(user -> user.openPrivateChannel())
				.flatMap(channel -> channel.sendMessageEmbeds(messageEmbedToSend))
				.queue(
					success -> interactionEvent.reply("Reply sent!").setEphemeral(true).queue(),
					error -> interactionEvent.reply("Failed to DM user.").setEphemeral(true).queue()
				);
			threadChannel.sendMessageEmbeds(embedToSendToThread).queue();
		}
	}
	}
