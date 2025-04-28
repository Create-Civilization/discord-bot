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
            Bot.LOGGER.error("Failed to make ticket because no channel is set");
            return;
        }
        if(event.getMessage().getChannelType() == ChannelType.PRIVATE && !event.getMessage().getAuthor().getId().equals(ConfigLoader.CLIENT_ID)){
            //Check if user already has a ticket
            TicketManager manager = new TicketManager();

            JDA jda = event.getJDA();
            Message message = event.getMessage();
            User author = message.getAuthor();
            Guild guild = jda.getGuildById(ConfigLoader.GUILD_ID);
            TextChannel helpTicketChannel = guild.getTextChannelById(ConfigLoader.HELP_TICKET_CHANNEL_ID);

            TicketEntry ticket = null;
            try {
                ticket = manager.getTicket(event.getMessage().getAuthor().getId(), "authorID");
            } catch (SQLException e) {
                Bot.LOGGER.error("Error getting ticket {}", e.getMessage(), e);
                return;
            }
            if(ticket == null){
                MessageEmbed embed = new EmbedBuilder()
                        .setColor(Color.decode("#32CD32"))
                        .setTitle(author.getGlobalName() + "'s Help Ticket")
                        .setDescription("waiting for thread")
                        .setFooter(author.getGlobalName() + " | " + author.getId(), author.getAvatarUrl())
                        .build();

                Message threadStartMsg = helpTicketChannel.sendMessageEmbeds(embed).complete();

                ThreadChannel threadChannel = threadStartMsg.createThreadChannel(author.getName() + "'s Help Ticket")
                        .complete();

                try {
                    manager.createTicket(author.getId(), threadChannel.getId(), threadStartMsg.getId());
                } catch (SQLException e){
                    Bot.LOGGER.error("Error creating ticket {}", e.getMessage(), e);
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

                message.addReaction(Emoji.fromUnicode("✅"));

                embed = new EmbedBuilder()
                        .setColor(Color.decode("#32CD32"))
                        .setTitle("Message Recived")
                        .setDescription(message.getContentRaw())
                        .setFooter(guild.getName() + " on " + getFormattedDateTime(), guild.getIconUrl())
                        .setAuthor(author.getName(), author.getAvatarUrl())
                        .build();

                threadChannel.sendMessageEmbeds(embed)
                        .queue();


            }

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
        return formattedDate + "at" + formattedTime;
    }
}
