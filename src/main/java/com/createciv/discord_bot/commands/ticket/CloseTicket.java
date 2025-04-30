package com.createciv.discord_bot.commands.ticket;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.managers.TicketManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.sql.SQLException;

public class CloseTicket extends SlashCommand {
    public CloseTicket() {
        super("close", "Close the current thread");
        addOption(new Option(OptionType.STRING, "reason", "The reason for closing the ticket" ,true));
        addOption(new Option(OptionType.BOOLEAN, "anonymous", "Do you want to send close message anonymously", false));
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

            TicketManager manager = new TicketManager();
            Channel channel = interactionEvent.getChannel();
            TicketEntry ticket = getChannelsTicket(channel, manager);

            Guild guild = interactionEvent.getGuild();
            if(guild == null){
                interactionEvent.reply("This command can only be used in a server").setEphemeral(true).queue();
                return;
            }

            if(ticket == null){
                interactionEvent.reply("Could not find ticket associated with this channel.").setEphemeral(true).queue();
                return;
            }

            if(interactionEvent.getOption("reason") == null) {
                interactionEvent.reply("You must provide a reason for closing the ticket").setEphemeral(true).queue();
                return;
            }

            String reason = interactionEvent.getOption("reason").getAsString();
            Boolean anonymous = interactionEvent.getOption("anonymous") != null && interactionEvent.getOption("anonymous").getAsBoolean();

            closeTicket(interactionEvent, manager, ticket, guild, reason, anonymous);

            ThreadChannel threadChannel = channel.getJDA().getThreadChannelById(ticket.threadChannelID);
            if(threadChannel != null) {
                try {
                    threadChannel.getManager().setLocked(true).queue();
                    threadChannel.getManager().setArchived(true).queue();
                } catch (Exception e) {
                    Bot.LOGGER.error("Failed to lock or archive thread: {}", e.getMessage(), e);
                }
            } else {
                Bot.LOGGER.error("Failed to find thread channel with ID: {}", ticket.threadChannelID);
            }

            interactionEvent.reply("Ticket Closed").setEphemeral(true).queue();
        } catch (Exception e) {
            if(!interactionEvent.isAcknowledged()) {
                interactionEvent.reply("An unexpected error occurred while processing your command").setEphemeral(true).queue();
            }
            throw new RuntimeException("Unexpected error in close ticket command: " + e.getMessage());
        }
    }

    private void closeTicket(SlashCommandInteractionEvent interactionEvent, TicketManager manager, TicketEntry ticket, Guild guild, String reason, Boolean anonymousMode) {
        try {
            manager.deleteTicket(ticket.id);
        } catch (SQLException e) {
            Bot.LOGGER.error("Failed to delete ticket: {}", e.getMessage(), e);
            interactionEvent.reply("Failed to delete ticket SQL error, please check logs").setEphemeral(true).queue();
            return;
        }

        try {
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.decode("#D70040"))
                    .setTitle("Ticket Closed")
                    .setDescription(String.format("Ticket has been closed%s by <@%s>. Reason: %s",
                            anonymousMode ? " anonymously" : "",
                            interactionEvent.getUser().getId(),
                            reason))
                    .setFooter(interactionEvent.getUser().getGlobalName() + " | " + interactionEvent.getUser().getId(), interactionEvent.getUser().getAvatarUrl())
                    .build();

            TextChannel helpTicketChannel = guild.getTextChannelById(ConfigLoader.HELP_TICKET_CHANNEL_ID);
            if(helpTicketChannel == null){
                throw new IllegalStateException("Could not find configured help ticket channel with ID: " + ConfigLoader.HELP_TICKET_CHANNEL_ID);
            }

            helpTicketChannel.editMessageEmbedsById(ticket.embedMessageID, embed)
                    .queue(null, error -> Bot.LOGGER.error("Failed to edit embed message: {}", error.getMessage(), error));

            embed = new EmbedBuilder()
                    .setColor(Color.decode("#D70040"))
                    .setTitle("Ticket Closed")
                    .setDescription(reason)
                    .setFooter(guild.getName() + " | " + guild.getId(), guild.getIconUrl())
                    .setAuthor(anonymousMode ? guild.getName() : interactionEvent.getUser().getGlobalName(),
                            null,
                            anonymousMode ? guild.getIconUrl() : interactionEvent.getUser().getAvatarUrl())
                    .build();

            JDA jda = interactionEvent.getJDA();
            MessageEmbed finalEmbed = embed;

            //Yes I did use AI for this. Sue me
            jda.retrieveUserById(ticket.authorID)
                    .queue(user -> {
                                user.openPrivateChannel()
                                        .queue(privateChannel -> {
                                                    privateChannel.sendMessageEmbeds(finalEmbed)
                                                            .queue(
                                                                    null,
                                                                    error -> Bot.LOGGER.error("Failed to send DM to user: {}", ticket.authorID, error)
                                                            );
                                                },
                                                error -> Bot.LOGGER.error("Failed to open private channel with user: {}", ticket.authorID, error));
                            },
                            error -> Bot.LOGGER.error("Failed to retrieve user: {}", ticket.authorID, error));

        } catch (Exception e) {
            new LoggingUtil().logError(e);
        }
    }

    private TicketEntry getChannelsTicket(Channel channel, TicketManager manager){
        TicketEntry ticket;
        try {
            ticket = manager.getTicket(channel.getId(), "threadChannelID");
        } catch (SQLException e) {
            Bot.LOGGER.error("Failed to get ticket in close ticket commands: {}", e.getMessage(), e);
            new LoggingUtil().logError(e);
            return null;
        }
        return ticket;
    }
}
