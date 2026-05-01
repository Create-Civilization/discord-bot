package com.createciv.discord_bot.commands.moderation;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.EmbedUtil;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.ModerationUtil;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class GetWhitelist extends SlashCommand {
	public GetWhitelist(){
		super("getwhitelist","see active whitelist for a user");
		addOption(new Option(OptionType.USER,"discorduser","insert discord user",false,false));
		addOption(new Option(OptionType.STRING,"mcuser","input a mc user",false,false));
	}
	@Override
	public void execute(SlashCommandInteractionEvent interactionEvent){
		if (ModerationUtil.isModeratorOnEvent(interactionEvent)){
			if (!Bot.DB_HEALTHY) {
				interactionEvent.reply("Database is not connected, try again later").queue();
				return;}
			WhitelistTable manager = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");
			String discordUserID = interactionEvent.getOption("discorduser", null, OptionMapping::getAsString);
			String playerID = interactionEvent.getOption("mcuser", null, OptionMapping::getAsString);
			WhitelistEntry entry = null;
			if (discordUserID != null){
				try { entry = manager.getActive(discordUserID);} catch (SQLException e) {LoggingUtil.error(e);}
				if (entry != null){
					MessageEmbed discordEmbed = EmbedUtil.WhitelistEmbed(entry);
					assert discordEmbed != null;
					LoggingUtil.getLogChannel().sendMessageEmbeds(discordEmbed).queue();
					interactionEvent.reply("whitelist fetched").queue();
				} else {
					interactionEvent.reply("no whitelist found").queue();
				}
			} else if (playerID != null){
				JsonObject response = MojangAPI.getPlayerInfo(playerID);
				if (response == null) {
					interactionEvent.reply("error occured, possibly invalid user").queue();
				} else if ((response.get("uuid").getAsString() != null) && response.get("username").getAsString() != null) {
					UUID playerUUID = UUID.fromString(response.get("uuid").getAsString());
					try { entry = manager.getActive(playerUUID);} catch (SQLException e) {LoggingUtil.error(e);}
					if (entry != null){
						MessageEmbed discordEmbed = EmbedUtil.WhitelistEmbed(entry);
						assert discordEmbed != null;
						LoggingUtil.getLogChannel().sendMessageEmbeds(discordEmbed).queue();
						interactionEvent.reply("whitelist fetched").queue();
					} else {
						interactionEvent.reply("no whitelist found").queue();
					}
				}
			} else {
				interactionEvent.reply("Please enter a valid Minecraft or Discord Username").setEphemeral(true).queue();
			}
		} else {
			interactionEvent.reply("You don't have permission to do that").queue();
		}
	}
}
