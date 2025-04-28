package com.createciv.discord_bot.listener.message;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.database.managers.TicketManager;
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
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class TicketCreator extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(ConfigLoader.HELP_TICKET_CHANNEL_ID == null){
            Bot.LOGGER.error("Failed to make ticket because no ChannelID is set in config");
            return;
        }
        if(ConfigLoader.GUILD_ID == null){
            Bot.LOGGER.error("Failed to make ticket as no GuildID is set in config");
            return;
        }
        if(event.getMessage().getChannelType() == ChannelType.PRIVATE && !event.getMessage().getAuthor().getId().equals(ConfigLoader.CLIENT_ID)){
            try {
                processTicketRequest(event);
            } catch (Exception e){
                Bot.LOGGER.error("Error occured processing ticket: {}", e.getMessage(), e);
                sendErrorToUser(event.getChannel(), "Error processing ticket", "A unknown error occurred, please try again later or contact a developer to fix the issue. ");
            }
        }
    }

    private void processTicketRequest(MessageReceivedEvent event){
        TicketManager manager = new TicketManager();
        JDA jda = event.getJDA();
        Message message = event.getMessage();
        User author = message.getAuthor();

        Guild guild;
        try {
            guild = jda.getGuildById(ConfigLoader.GUILD_ID);
            if(guild == null){
                throw new IllegalStateException("Could not find guild with ID: " + ConfigLoader.GUILD_ID);
            }
        } catch (Exception e){
            Bot.LOGGER.error("Failed to get guild: {}", e.getMessage(), e);
            sendErrorToUser(event.getChannel(), "Server Error", "Could not locate the server. Please try again later");
            return;
        }

        TextChannel helpTicketChannel;
        try {
            helpTicketChannel = guild.getTextChannelById(ConfigLoader.HELP_TICKET_CHANNEL_ID);
            if(helpTicketChannel == null){
                throw new IllegalStateException("Could not find configured help ticket channel with ID: " + ConfigLoader.GUILD_ID);
            }
        } catch (Exception e) {
            Bot.LOGGER.error("Failed to find help ticket channel: {} ", e.getMessage(), e);
            sendErrorToUser(event.getChannel(), "Channel Error", "Could not find configured help ticket channel, please contact a developer to fix this issue.");
            return;
        }

        TicketEntry ticket;
        try {
            ticket = manager.getTicket(author.getId(), "authorID");
        } catch (SQLException e){
            Bot.LOGGER.error("Database error getting ticket: {}", e.getMessage(), e);
            sendErrorToUser(event.getChannel(), "Database Error", "A critical error occured in the ticket database, please contact a developer to fix this issue.");
            return;
        }

        if(ticket == null){
            handleNewTicket(event, manager, message, author, guild, helpTicketChannel);
        } else{
            handleExistingTicket(event, ticket, message, author, guild);
        }

    }

    private void handleNewTicket(MessageReceivedEvent event, TicketManager manager, Message message, User author, Guild guild, TextChannel helpTicketChannel){
        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#32CD32"))
                .setTitle(author.getGlobalName() + "'s Help Ticket")
                .setDescription("waiting for thread")
                .setFooter(author.getGlobalName() + " | " + author.getId(), author.getAvatarUrl())
                .build();

        Message threadStartMsg;
        try{
            threadStartMsg = helpTicketChannel.sendMessageEmbeds(embed).complete();
        } catch (Exception e){
            Bot.LOGGER.error("Failed to send starting embed: {}", e.getMessage(), e);
            sendErrorToUser(event.getChannel(), "Ticket Failed", "Failed to create ticket. Please try again later.");
            return;
        }

        ThreadChannel threadChannel;
        try {
            threadChannel = threadStartMsg.createThreadChannel(author.getName() + "'s Help Ticket").complete();
        } catch (Exception e){
            Bot.LOGGER.error("Failed to create thread channel: {}", e.getMessage(), e);
            threadStartMsg.delete().queue();
            sendErrorToUser(event.getChannel(), "Ticket Failed", "Failed to create ticket. Please try again later.");
            return;
        }

        try {
            manager.createTicket(author.getId(), threadChannel.getId(), threadStartMsg.getId());
        } catch (SQLException e) {
            Bot.LOGGER.error("Database error creating ticket: {}", e.getMessage(), e);
            threadChannel.delete().queue();
            threadStartMsg.delete().queue();
            sendErrorToUser(event.getChannel(), "Ticket Failed", "Failed to create ticket. Please try again later.");
            return;
        }

        embed = new EmbedBuilder()
                .setColor(Color.decode("#32CD32"))
                .setTitle(author.getGlobalName() + "'s Help Ticket")
                .setDescription("<#" + threadChannel.getId() + ">")
                .setFooter(author.getGlobalName() + " | " + author.getId(), author.getAvatarUrl())
                .build();

        threadStartMsg.editMessageEmbeds(embed).queue();

        embed = new EmbedBuilder()
                .setColor(Color.decode("#bfbfbf"))
                .setTitle("A new ticket has been made")
                .setDescription("To respond to this ticket use \\`/reply\\` every other message will be ignored. To close the ticket do \\`/close\\` this ticket will automatically close after \\`" + ConfigLoader.TICKET_EXPIRY_TIME_SECONDS / 60 / 60 / 24 + "\\` days")
                .build();


        threadChannel.sendMessageEmbeds(embed).queue();

        message.addReaction(Emoji.fromUnicode("U+2705")).queue();

        embed = new EmbedBuilder()
                .setColor(Color.decode("#32CD32"))
                .setTitle("Message Received")
                .setDescription(message.getContentRaw())
                .setFooter(guild.getName() + " on " + getFormattedDateTime(), guild.getIconUrl())
                .setAuthor(author.getName(), author.getAvatarUrl())
                .build();

        threadChannel.sendMessageEmbeds(embed).queue();
    }

    private void handleExistingTicket(MessageReceivedEvent event, TicketEntry ticket, Message message, User author, Guild guild){
        ThreadChannel channel;
        try {
            channel = guild.getThreadChannelById(ticket.getThreadChannelID());
            if(channel == null){
                throw new IllegalStateException("Thread channel not found for existing ticket ID: " + ticket.getThreadChannelID());
            }
        } catch (Exception e) {
            Bot.LOGGER.error("Failed to get thread channel: {}", e.getMessage(), e);
            sendErrorToUser(event.getChannel(), "Ticket Error", "Failed to find your ticket thread. Please contact a developer.");
            return;
        }

        MessageEmbed embed = new EmbedBuilder()
                .setColor(Color.decode("#32CD32"))
                .setDescription(message.getContentRaw())
                .setFooter(guild.getName() + " on " + getFormattedDateTime(), guild.getIconUrl())
                .setAuthor(author.getName(), null, author.getAvatarUrl())
                .build();

        try {
            channel.sendMessageEmbeds(embed).queue();
            message.addReaction(Emoji.fromUnicode("U+2705")).queue();
        } catch (Exception e){
            Bot.LOGGER.error("Failed to forward message to thread: {}", e.getMessage(), e);
            sendErrorToUser(event.getChannel(), "Ticket Error", "Failed to send your message to your ticket thread. Please contact a developer. ");
        }
    }

    private void sendErrorToUser(MessageChannel channel, String title, String message){
        try {
            MessageEmbed errorEmbed = new EmbedBuilder()
                    .setColor(Color.RED)
                    .setTitle("❌ " + title)
                    .setDescription(message)
                    .setTimestamp(java.time.Instant.now())
                    .build();

            channel.sendMessageEmbeds(errorEmbed).queue();
        }catch (Exception e){
            Bot.LOGGER.error("Cant notify user of error: {}", e.getMessage(), e);
        }
    }

    public String getFormattedDateTime() {
        LocalDateTime now = LocalDateTime.now();
        String formattedDate = (now.getMonthValue()) + "/" + now.getDayOfMonth() + "/" + now.getYear();
        int hours = now.getHour();
        String ampm = hours >= 12 ? "PM" : "AM";
        hours = hours % 12;
        if (hours == 0) hours = 12;

        String minutes = String.format("%02d", now.getMinute());

        String formattedTime = hours + ":" + minutes + " " + ampm;
        return formattedDate + " at " + formattedTime;
    }
}
