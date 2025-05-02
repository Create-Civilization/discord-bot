package com.createciv.discord_bot.commands.ticket;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.TicketManager;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReplyToTicket extends SlashCommand {

    public ReplyToTicket() {
        super("reply", "Replies to the current ticket");
        addOption(new Option(OptionType.STRING, "message", "The message you want to reply with", true));
        addOption(new Option(OptionType.BOOLEAN, "anonymous", "Do you want to send close message anonymously", false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        try{
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

            Guild guild = interactionEvent.getGuild();
            if(guild == null){
                interactionEvent.reply("This command can only be used in a server").setEphemeral(true).queue();
                return;
            }

            TicketManager manager = DatabaseRegistry.getTicketManager();

            Channel channel = interactionEvent.getChannel();
            TicketEntry ticket = getChannelsTicket(channel, manager);

            if(ticket == null){
                interactionEvent.reply("Could not find ticket associated with this channel.").setEphemeral(true).queue();
                return;
            }

            JDA jda = interactionEvent.getJDA();
            String message = interactionEvent.getOption("message").getAsString();
            boolean anonymous = interactionEvent.getOption("anonymous") != null && interactionEvent.getOption("anonymous").getAsBoolean();
            ThreadChannel threadChannel = channel.getJDA().getThreadChannelById(ticket.threadChannelID);


            MessageEmbed embed = new EmbedBuilder()
                    .setColor(Color.decode("#32CD32"))
                    .setTitle("Response Received")
                    .setDescription(message)
                    .setFooter(guild.getName() + " on " + getFormattedDateTime(), guild.getIconUrl())
                    .setAuthor(anonymous ? guild.getName() : interactionEvent.getUser().getGlobalName(),
                            null,
                            anonymous ? guild.getIconUrl() : interactionEvent.getUser().getAvatarUrl())
                    .build();

            MessageEmbed finalEmbed = embed;
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


            try {
                manager.updateTicketActivity(ticket.id);
            } catch (SQLException e){
                Bot.LOGGER.error("Failed to update ticket activity: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to update ticket activity: "+ e.getMessage());
            }

            embed = new EmbedBuilder()
                    .setColor(Color.decode("#32CD32"))
                    .setTitle("Message Sent")
                    .setDescription(message)
                    .setFooter(guild.getName() + " on " + getFormattedDateTime(), guild.getIconUrl())
                    .setAuthor(anonymous ? guild.getName() : interactionEvent.getUser().getGlobalName(),
                            null,
                            anonymous ? guild.getIconUrl() : interactionEvent.getUser().getAvatarUrl())
                    .build();


            threadChannel.sendMessageEmbeds(embed).queue(
                    null,
                    error -> Bot.LOGGER.error("Failed to send embed to thread")
            );

            interactionEvent.reply("Message Sent").setEphemeral(true).queue();
        } catch (Exception e) {
            Bot.LOGGER.error("Error in reply command {}", e.getMessage(), e);
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

    private TicketEntry getChannelsTicket(Channel channel, TicketManager manager){
        TicketEntry ticket;
        try {
            ticket = manager.getTicket(channel.getId(), "threadChannelID");
        } catch (SQLException e) {
            Bot.LOGGER.error("Failed to get ticket in close ticket commands: {}", e.getMessage(), e);
            return null;
        }
        return ticket;
    }
}
