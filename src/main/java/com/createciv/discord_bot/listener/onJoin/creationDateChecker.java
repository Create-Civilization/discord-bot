package com.createciv.discord_bot.listener.onJoin;

import com.createciv.discord_bot.util.LoggingUtil;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.OffsetDateTime;
import static com.createciv.discord_bot.Bot.LOGGER;

public class creationDateChecker extends ListenerAdapter {
    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        Member discordName = event.getMember();
        OffsetDateTime accCreateDate = discordName.getTimeCreated();
        OffsetDateTime minDate = OffsetDateTime.now().minusMonths(1);
        if (accCreateDate.isAfter(minDate)){
            Guild guild = event.getGuild();
            MessageEmbed embedToUser = new EmbedBuilder()
                    .setColor(Color.red)
                    .setTitle("Discord Account Creation Too Recent")
                    .setDescription("Sadly, you are unable to join Create: Civilization because your discord account was created under a month ago. Please try to rejoin once your account has aged a little bit. We are sorry for the inconvenience and hope you'll be playing with us soon!")
                    .setFooter(guild.getName() + " | " + guild.getId(), guild.getIconUrl())
                    .build();
            discordName.getUser().openPrivateChannel()
                    .flatMap(channel -> channel.sendMessageEmbeds(embedToUser))
                    .queue(
                            success -> kickUser(guild, discordName),
                            error -> {
                                System.out.println("Failed to send DM: " + error.getMessage());
                                kickUser(guild, discordName);
                            }
                    );
        }
    }
    private void kickUser(Guild guild, Member member){
        guild.kick(member)
                .reason("Account creation date too new")
                .queue(
     //                   success->LoggingUtil.logUserKick(member.getUser(),"Account creation date too new"),
                        error -> LOGGER.error("An error occurred while kicking a user with an account made too recently")
                );
    }
}
