package com.createciv.discord_bot.commands.server.whitelist;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.sql.SQLException;

import static com.createciv.discord_bot.Bot.LOGGER;

@SuppressWarnings("unused")
public class RemoveWhitelist extends SlashCommand {

	public RemoveWhitelist() {
		super("remove_whitelist", "Remove yourself from whitelist");
	}

	@Override
	public void execute(SlashCommandInteractionEvent interactionEvent) throws SQLException {
		if (!Bot.DB_HEALTHY) {
			interactionEvent.reply("Database is not connected, try again later").queue();
			return;
		}
		String userID = interactionEvent.getUser().getId();
		WhitelistTable whitelistTable = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");
		WhitelistEntry whitelistEntry = whitelistTable.get(userID);
		if (whitelistEntry == null) {
			interactionEvent.reply("You are not whitelisted.").setEphemeral(true).queue();
			return;
		}
		Guild guild = interactionEvent.getGuild();
		Member mem = interactionEvent.getMember();
		assert guild != null;
		Role whitelistedRole = guild.getRoleById(ConfigLoader.WHITELIST_ROLE_ID);

		whitelistTable.remove(userID);
		try {
			assert whitelistedRole != null;
			assert mem != null;
			guild.removeRoleFromMember(mem, whitelistedRole).queue();
		} catch (Exception e) {
			System.out.println("Guild" + guild);
			System.out.println("Member" + mem);
			System.out.println("Role" + whitelistedRole);
			LOGGER.error("Failed to add role to whitelisted user", e);
		}
		interactionEvent.reply("You have successfully been removed from the whitelist").setEphemeral(true).queue();

		//@TODO Fix Logging
		//LoggingUtil.log(Color.red, "Whitelist Removed", String.format("%s has been removed from the whitelist", usernameCacheEntry.username));
		//new LoggingUtil().logRemoveWhitelist(whitelistEntry, interactionEvent.getUser());
	}
}