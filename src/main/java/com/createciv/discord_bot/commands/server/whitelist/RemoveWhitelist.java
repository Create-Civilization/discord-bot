package com.createciv.discord_bot.commands.server.whitelist;

import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;

import static com.createciv.discord_bot.Bot.LOGGER;

@SuppressWarnings("unused")
public class RemoveWhitelist extends SlashCommand {

    public RemoveWhitelist() {
        super("remove_whitelist", "Remove yourself from whitelist");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        try {
            String userID = interactionEvent.getUser().getId();
            WhitelistTable whitelistTable = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");
            WhitelistEntry whitelistEntry = whitelistTable.get(userID);
            if (whitelistEntry == null) {
                interactionEvent.reply("You are not whitelisted.").setEphemeral(true).queue();
                return;
            }

            whitelistTable.remove(userID);
            interactionEvent.reply("You have successfully been removed from the whitelist").setEphemeral(true).queue();

            //@TODO Fix Logging
            //new LoggingUtil().logRemoveWhitelist(whitelistEntry, interactionEvent.getUser());

        } catch (SQLException e) {
            LOGGER.error("Error in RemoveWhitelist command", e);
            new LoggingUtil().logError(e);
        }
    }
}
