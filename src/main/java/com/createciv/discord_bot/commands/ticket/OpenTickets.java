package com.createciv.discord_bot.commands.ticket;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.TicketManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;
import java.util.List;

public class OpenTickets extends SlashCommand {

    public OpenTickets() {
        super("open_tickets", "Gets all the currently open tickets");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        try {
            if(ConfigLoader.ADMIN_ROLE_IDS.isEmpty()){
                Bot.LOGGER.error("No admin roles selected. Please set this in the config to continue");
                interactionEvent.reply("Admin roles are not configured. Please configure them to use this command")
                        .setEphemeral(true).queue();
                return;
            }

            if(interactionEvent.getMember() == null) {
                interactionEvent.reply("Could not find member information").setEphemeral(true).queue();
                return;
            }

            boolean hasAdminRole = interactionEvent.getMember().getRoles().stream()
                    .map(Role::getId)
                    .anyMatch(ConfigLoader.ADMIN_ROLE_IDS::contains);

            if(!hasAdminRole){
                interactionEvent.reply("You do not have permission to run this command").setEphemeral(true).queue();
                return;
            }

            TicketManager manager = DatabaseRegistry.getTicketManager();
            List<TicketEntry> tickets;
            try {
                tickets = manager.getAllTickets();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to fetch all tickets: " + e.getMessage());
            }

            if(tickets.isEmpty()){
                interactionEvent.reply("No tickets found").setEphemeral(true).queue();
                return;
            }

            StringBuilder message = new StringBuilder();
            JDA jda = interactionEvent.getJDA();
            for (TicketEntry ticket : tickets){
                Channel channel = jda.getThreadChannelById(ticket.threadChannelID);
                if(channel == null) throw new RuntimeException("Found missing thread in database. Please Fix");
                message.append(" <#").append(channel.getId()).append(">");
            }

            interactionEvent.reply(message.toString()).setEphemeral(true).queue();
        } catch (Exception e) {
            Bot.LOGGER.error("Error occurred in OpenTickets: {}", e.getMessage(), e);
            interactionEvent.reply("A fatal error occurred please try again later.");
            return;
        }
    }
}
