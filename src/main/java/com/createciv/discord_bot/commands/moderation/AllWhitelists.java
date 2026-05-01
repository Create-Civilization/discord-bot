package com.createciv.discord_bot.commands.moderation;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.EmbedUtil;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.ModerationUtil;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import javax.sound.midi.SysexMessage;
import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AllWhitelists extends SlashCommand {
	//TODO fix discord embed @'s
	public AllWhitelists() {super("getallwhitelists", "see all whitelists for a user");
		addOption(new Option(OptionType.USER,"discorduser","input a discorduser",false,false));
		addOption(new Option(OptionType.STRING,"mcuser","input a mc user",false,false));
	}
	@Override
		public void execute(SlashCommandInteractionEvent interactionEvent) throws SQLException {
		//check if user is allowed to use command
		if (ModerationUtil.isModeratorOnEvent(interactionEvent)) {
			TextChannel logChannel = LoggingUtil.getLogChannel();
			assert logChannel != null;
			if (!Bot.DB_HEALTHY) {
				interactionEvent.reply("Database is not connected, try again later").queue();
				return;
			}
			//determine whether to make a discord embed or mc user embed
			WhitelistTable manager = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");
			String discordUserID = interactionEvent.getOption("discorduser", null, OptionMapping::getAsString);
			String playerID = interactionEvent.getOption("mcuser", null, OptionMapping::getAsString);
			if (discordUserID != null) {
				List<WhitelistEntry> entries = manager.getAll(discordUserID);
				if (entries.isEmpty()) {
					interactionEvent.reply("No associated entries").queue();
					return;
				}
				MessageEmbed embed = EmbedUtil.getAllMinecraftFormatter(discordUserID, entries);
				logChannel.sendMessageEmbeds(embed).queue();
				interactionEvent.reply("History Fetched").queue();
			} else if (playerID != null) {
				JsonObject response = MojangAPI.getPlayerInfo(playerID);
				if (response == null) {
					interactionEvent.reply("error occured, possibly invalid user").queue();
				} else if ((response.get("uuid").getAsString() != null) && response.get("username").getAsString() != null) {
					String mcuser = response.get("username").getAsString();
					UUID formatedUUID = UUID.fromString(response.get("uuid").getAsString());
					List<WhitelistEntry> entries = manager.getAll(formatedUUID);
					if (entries.isEmpty()) {
						interactionEvent.reply("No associated entries").queue();
						return;
					}
					MessageEmbed embed = EmbedUtil.getAllDiscordFormatter(mcuser,entries);
					assert logChannel != null;
					logChannel.sendMessageEmbeds(embed).queue();
					interactionEvent.reply("History Fetched").queue();
				} else {
					interactionEvent.reply("No player exists").queue();
				}
			} else {
				interactionEvent.reply("Input either a discord user or a minecraft user").queue();
			}
		}else {
			interactionEvent.reply("Not Authorized").setEphemeral(true).queue();
		}
	}

}
