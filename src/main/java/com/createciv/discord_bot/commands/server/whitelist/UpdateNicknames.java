package com.createciv.discord_bot.commands.server.whitelist;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class UpdateNicknames extends SlashCommand {

    public UpdateNicknames() {
        super("update_nicknames", "Update nicknames of all discord members");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        if (!hasPermission(interactionEvent)) return;
        List<WhitelistEntry> whitelistEntries;
        WhitelistManager whitelistManager  = (WhitelistManager) DatabaseRegistry.getTableManager("whitelist");
        try {
            whitelistEntries = whitelistManager.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Guild guild = interactionEvent.getGuild();
        List<Member> members = Objects.requireNonNull(interactionEvent.getGuild()).getMembers();

        for (WhitelistEntry entry : whitelistEntries) {
            if (members.stream().toList().contains(guild.getMemberById(entry.discordID))) {
                Member member = guild.getMemberById(entry.discordID);
                member.modifyNickname(entry.username);
            }
        }


    }


    private boolean hasPermission(SlashCommandInteractionEvent interactionEvent) {

        if (ConfigLoader.ADMIN_ROLE_IDS.isEmpty()) {
            Bot.LOGGER.error("No admin roles selected. Please set this in the config to continue");
            interactionEvent.reply("Admin roles are not configured. Please configure them to use this command")
                    .setEphemeral(true).queue();
            return false;
        }

        if (interactionEvent.getMember() == null) {
            interactionEvent.reply("Could not find member information").setEphemeral(true).queue();
            return false;
        }

        boolean hasAdminRole = interactionEvent.getMember().getRoles().stream()
                .map(Role::getId)
                .anyMatch(ConfigLoader.ADMIN_ROLE_IDS::contains);

        if (!hasAdminRole) {
            interactionEvent.reply("You do not have permission to run this command").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}
