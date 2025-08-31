package com.createciv.discord_bot.commands.server.whitelist;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class PurgeWhitelist extends SlashCommand {

    public PurgeWhitelist() {
        super("purge_whitelist", "purge whitelist of all non members");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        try {

            if (!hasPermission(interactionEvent)) return;

            WhitelistManager manager = (WhitelistManager) DatabaseRegistry.getTableManager("whitelist");

            List<WhitelistEntry> whitelistEntries = manager.getAll();

            List<Member> members = interactionEvent.getGuild().getMembers();

            for (WhitelistEntry entry : whitelistEntries) {
                if (!members.stream().toList().contains(interactionEvent.getGuild().getMemberById(entry.discordID))) {
                    manager.removeWithDiscordID(entry.discordID);
                    //TODO add un-whitelist here

                }
            }


        } catch (Exception e) {
            Bot.LOGGER.error("Error occurred in PurgeWhitelist: {}", e.getMessage(), e);
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
