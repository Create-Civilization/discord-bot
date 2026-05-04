package com.createciv.discord_bot.commands.helpticketcmds;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.EmbedUtil;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.TicketTable;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;

public class CloseTicket extends SlashCommand {
	//TODO make ticket closing anon option
	public CloseTicket(){
		super("close","close this ticket");
		addOption(new Option(OptionType.STRING,"reason","reason why ticket was closed, will be set to handled if not given",false,false));
	}
	@Override
	public void execute(SlashCommandInteractionEvent interactionEvent){
		if (!Bot.DB_HEALTHY) {
			interactionEvent.reply("Database is not connected, try again later").queue();
			return;
		}
		JDA jda = interactionEvent.getJDA();
		User sender = interactionEvent.getUser();
		Guild guild = jda.getGuildById(ConfigLoader.GUILD_ID);
		if (!interactionEvent.getChannel().getType().isThread()){
			interactionEvent.reply("Use command in a valid thread").setEphemeral(true).queue();}
		ThreadChannel threadChannel = interactionEvent.getChannel().asThreadChannel();
		TicketTable manager = (TicketTable) DatabaseRegistry.getTableManager("tickets");
		TicketEntry ticket = null;
		try{
			ticket = manager.getFromThreadID(threadChannel.getId());
		} catch (SQLException e){
			LoggingUtil.error(e);
		}
		if (ticket != null){
			String ticketMakerID = ticket.getAuthorID();
			String response = "Issue Handled";
			if (interactionEvent.getOption("reason") != null) {response = interactionEvent.getOption("reason").getAsString();}
			MessageEmbed closingEmbed = EmbedUtil.InternalTextTicketMessage(sender,"Reason:" + response,"Ticket Closed", Timestamp.from(Instant.now()),"#FF1D15");
			jda.retrieveUserById(ticketMakerID)
				.flatMap(user -> user.openPrivateChannel())
				.flatMap(channel -> channel.sendMessageEmbeds(closingEmbed))
				.queue(
					success -> interactionEvent.reply("Reply sent!").setEphemeral(true).queue(),
					error -> interactionEvent.reply("Failed to DM user.").setEphemeral(true).queue()
				);
			TextChannel helpTicketChannel = guild.getTextChannelById(ConfigLoader.HELP_TICKET_CHANNEL_ID);
			if (helpTicketChannel == null){return;}
			helpTicketChannel.retrieveMessageById(ticket.getEmbedMessageID()).queue(msg->{
				msg.editMessageEmbeds(closingEmbed).queue();},
				throwable -> {
				LoggingUtil.log(Color.red,"Help Ticket Issue","Help ticket embed not found");
			});
			try {
				manager.remove(ticketMakerID);
			} catch (SQLException e) {
				LoggingUtil.error(e);
			}
			threadChannel.getManager().setLocked(true).queue();
		}
	}
}
