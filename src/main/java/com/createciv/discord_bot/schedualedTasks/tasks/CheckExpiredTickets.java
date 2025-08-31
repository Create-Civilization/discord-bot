package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.TicketManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.awt.*;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckExpiredTickets extends ScheduledTask {

    public CheckExpiredTickets() {
        super("check_expired_tickets", TimeUnit.MINUTES, 15);
    }

    @Override
    public void execute() {

        JDA jda = Bot.API;

        List<TicketEntry> ticketEntries;
        try {
            TicketManager manager = (TicketManager) DatabaseRegistry.getTableManager("tickets");
            ticketEntries = manager.getExpiredTickets();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        for (TicketEntry ticketEntry : ticketEntries) {
            //Make Message To Edit
            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.decode("#D70040"))
                    .setTitle("Ticket Closed")
                    .setDescription(String.format("Ticket has been closed by <@%s>. Reason: Ticket Expired After \\`%s\\` days. If this ticket is still relevant, please make a new one.",
                            Bot.BOT.getId(),
                            ConfigLoader.TICKET_EXPIRY_TIME_SECONDS / 60 / 60 / 24))
                    .setFooter(Bot.BOT.getName() + " | " + Bot.BOT.getId(), Bot.BOT.getAvatarUrl())
                    .build();

            ThreadChannel channel = jda.getThreadChannelById(ticketEntry.threadChannelID);
            Message message = channel.retrieveMessageById(ticketEntry.embedMessageID).complete();
            message.editMessageEmbeds(embed).queue();

            //Send Message
            embed = new EmbedBuilder()
                    .setColor(Color.decode("#D70040"))
                    .setTitle("Ticket Closed Due To Expiry")
                    .setDescription(String.format("Ticket has been closed by <@%s>. Reason: Ticket Expired After \\`%s\\` days. If this ticket is still relevant, please make a new one.",
                            Bot.BOT.getId(),
                            ConfigLoader.TICKET_EXPIRY_TIME_SECONDS / 60 / 60 / 24))
                    .setFooter(Bot.BOT.getName() + " | " + Bot.BOT.getId(), Bot.BOT.getAvatarUrl())
                    .build();

            User author = jda.getUserById(ticketEntry.authorID);

            MessageEmbed finalEmbed = embed;
            author.openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessageEmbeds(finalEmbed).queue();
            });
        }


    }
}
