package com.createciv.discord_bot.listener.modal;

import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import static com.createciv.discord_bot.Bot.LOGGER;

public class WhitelistListener extends ListenerAdapter {

	//TODO change players nicknames on the server to MC name after the whitelist goes through

	@Override
	public void onModalInteraction(@NotNull ModalInteractionEvent event) {
		if (!event.getModalId().equals("whitelist")) {
			return;
		}

		String username = Objects.requireNonNull(event.getValue("username")).getAsString();
		String referred = Objects.requireNonNull(event.getValue("referral")).getAsString();
		JsonObject response = MojangAPI.getPlayerInfo(username);
		if (response == null) {
			event.reply("A severe error occurred. Please try again later").setEphemeral(true).queue();
			return;
		}

		//Check if we got an invalid username
		if (response.get("reason") != null) {
			event.reply(response.get("reason").getAsString()).setEphemeral(true).queue();
			return;
		}

		if (response.get("uuid").getAsString() != null) {
			UUID formatedUUID = UUID.fromString(response.get("uuid").getAsString());

			WhitelistEntry entry = new WhitelistEntry.Builder()
				.playerUUID(formatedUUID)
				.discordID(event.getUser().getId())
				.referralReason(referred)
				.build();

			WhitelistTable manager = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");

			try {
				manager.add(entry);
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}

			Guild guild = event.getGuild();
			Member user = event.getMember();
			Role whitelistedRole = guild.getRoleById(ConfigLoader.WHITELIST_ROLE_ID);
			try {
				guild.addRoleToMember(user, whitelistedRole).queue();
			} catch (Exception e) {
				System.out.println("Guild" + guild);
				System.out.println("Member" + user);
				System.out.println("Role" + whitelistedRole);
				LOGGER.error("Failed to add role to whitelisted user", e);
			}

			event.reply("You have been successfully whitelisted").setEphemeral(true).queue();
			LoggingUtil.log(Color.green, "New Whitelist", String.format("%s has been added to the whitelist.", username));
			return;
		}

		event.reply("Unknown error occurred, If this continues please contact a developer").setEphemeral(true).queue();
	}
}