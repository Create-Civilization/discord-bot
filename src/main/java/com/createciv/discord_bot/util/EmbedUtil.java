package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;

public class EmbedUtil {
	public static MessageEmbed BasicEmbed (String title, String description, Color color){
		User bot = Bot.BOT;
		Timestamp timesent = Timestamp.from(Instant.now());
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String readable = sdf.format(timesent);
		MessageEmbed embed = new EmbedBuilder()
			.setTitle(title)
			.setDescription(description)
			.setColor(color)
			.setFooter(bot.getName() + " on " + readable, bot.getAvatarUrl())
			.build();
		return embed;
	}
	//TODO make it so it takes optional input of link to help ticket thread
	public static MessageEmbed StarterTicketChannelEmbed(User sender,String description){
		String senderID = sender.getId();
		String username = sender.getName();
		MessageEmbed starter = new EmbedBuilder()
			.setTitle("Help Ticket for " + username)
			.setDescription(description)
			.setColor(Color.decode("#8CC084"))
			.setFooter(username + " | " + senderID, sender.getAvatarUrl())
			.build();
		return starter;
	}
	public static MessageEmbed InternalTextTicketMessage(User sender, String message, String title, Timestamp timesent, String color){
		User bot = Bot.BOT;
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String readable = sdf.format(timesent);
		String senderID = sender.getId();
		String username = sender.getName();
		MessageEmbed embed = new EmbedBuilder()
			.setAuthor(username,"https://discord.com/users/" + senderID,sender.getAvatarUrl())
			.setTitle(title)
			.setColor(Color.decode(color))
			.setDescription(message)
			.setFooter(bot.getName() + " on " + readable, bot.getAvatarUrl())
			.build();
		return embed;
	}
	public static MessageEmbed WhitelistEmbed(WhitelistEntry entry){
		User bot = Bot.BOT;
		Guild guild = Bot.API.getGuildById(ConfigLoader.GUILD_ID);
		Timestamp timeCreated = entry.getCreatedAt();
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		String readable = sdf.format(timeCreated);
		JsonObject response = MojangAPI.getPlayerInfo(entry.getPlayerUUID().toString());
		String mcuserName;
		if ((response != null) && response.get("username").getAsString()!= null ) {
			mcuserName = response.get("username").getAsString();}
		else{return null;}
		MessageEmbed.Field createdAtField = new MessageEmbed.Field("Created at:",readable,true);
		MessageEmbed.Field entryIDField = new MessageEmbed.Field("Entry ID:",entry.getEntryID().toString(),true);
		MessageEmbed.Field discordUser = new MessageEmbed.Field("Discord User:","<@" + entry.getDiscordID() + ">", true);
		MessageEmbed.Field mcUser = new MessageEmbed.Field("Minecraft User:", mcuserName,true);
		MessageEmbed.Field mcUUID = new MessageEmbed.Field("Minecraft UUID:",entry.getPlayerUUID().toString(),false);
		MessageEmbed.Field referralField = new MessageEmbed.Field("Referral Reason:",entry.getReferralReason(),false);
		MessageEmbed.Field activeField = new MessageEmbed.Field("Active:", entry.getActive().toString(),true);
		MessageEmbed embed = new EmbedBuilder()
			.setAuthor(bot.getName(),bot.getAvatarUrl())
			.setTitle(mcuserName + "'s Whitelist Information")
			.addField(discordUser)
			.addField(mcUser)
			.addField(mcUUID)
			.addField(createdAtField)
			.addField(entryIDField)
			.addField(activeField)
			.addField(referralField)
			.build();
		return embed;
	}
	public static MessageEmbed getAllDiscordFormatter (String playerUUID, List<WhitelistEntry> entries){
		StringBuilder userEntries = new StringBuilder();
		StringBuilder ids = new StringBuilder();
		String activeWhitelist = "no active whitelist";
		for (WhitelistEntry entry : entries){
			userEntries.append("<@").append(entry.getDiscordID()).append(">\n");
			ids.append(entry.getEntryID().toString()).append("\n");
			if (entry.getActive()){
				activeWhitelist = "<@" + entry.getDiscordID() + ">\n";
			}}
		MessageEmbed.Field activeField = new MessageEmbed.Field("**Active Whitelist:**", activeWhitelist,false);
		MessageEmbed.Field field1 = new MessageEmbed.Field("All Related Discord Users ", userEntries.toString(),true);
		MessageEmbed.Field field2 = new MessageEmbed.Field("Entry IDs ", ids.toString(),true);
		MessageEmbed embed = new EmbedBuilder()
			.setTitle("Whitelist Entries of **" + playerUUID + "**")
			.setDescription("**Total Whitelists**: " + String.valueOf(entries.size()))
			.addField(activeField)
			.addField(field1)
			.addField(field2)
			.setColor(Color.decode("#809ae8"))
			.setFooter(Bot.BOT.getName() + " | " + Bot.BOT.getId(), Bot.BOT.getAvatarUrl())
			.build();
		return embed;}
	public static MessageEmbed getAllMinecraftFormatter(String discordUser, List<WhitelistEntry> entries){
		StringBuilder userEntries = new StringBuilder();
		StringBuilder ids = new StringBuilder();
		String activeWhitelist = "no active whitelist";
		for (WhitelistEntry entry : entries){
			JsonObject response = MojangAPI.getPlayerInfo(entry.getPlayerUUID().toString());
			if ((response != null) && response.get("username").getAsString()!= null ) {
				userEntries.append(response.get("username").getAsString()).append("\n");
				ids.append(entry.getEntryID().toString()).append("\n");
				if (entry.getActive()){
					activeWhitelist = response.get("username").getAsString() + "\n";
				}}
		}
		MessageEmbed.Field activeWhitelistField = new MessageEmbed.Field("**Active Whitelist:**", activeWhitelist,false);
		MessageEmbed.Field field1 = new MessageEmbed.Field("All Related MC Users ", userEntries.toString(),true);
		MessageEmbed.Field field2 = new MessageEmbed.Field("Entry IDs ", ids.toString(),true);
		MessageEmbed embed = new EmbedBuilder()
			.setTitle("Whitelist Entries of " + discordUser)
			.setDescription("**Total Whitelists**: " + String.valueOf(entries.size()))
			.addField(activeWhitelistField)
			.addField(field1)
			.addField(field2)
			.setColor(Color.decode("#809ae8"))
			.setFooter(Bot.BOT.getName() + " | " + Bot.BOT.getId(), Bot.BOT.getAvatarUrl())
			.build();
		return embed;
	}
}
