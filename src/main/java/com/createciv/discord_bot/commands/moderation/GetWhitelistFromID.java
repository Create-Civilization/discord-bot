package com.createciv.discord_bot.commands.moderation;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.EmbedUtil;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.ModerationUtil;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.sql.SQLException;

public class GetWhitelistFromID extends SlashCommand {
	public GetWhitelistFromID(){
		super("getwhitelistfromid","get a whitelist from entry ID");
		addOption(new Option(OptionType.INTEGER,"id","insert ID, should be a number",true,false));
	}

	@Override
	public void execute(SlashCommandInteractionEvent interactionEvent){
		if (ModerationUtil.isModeratorOnEvent(interactionEvent)) {
			if (!Bot.DB_HEALTHY) {
				interactionEvent.reply("Database is not connected, try again later").setEphemeral(true).queue();
				return;
			}
			WhitelistTable manager = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");
			Integer entryID = interactionEvent.getOption("id").getAsInt();
			WhitelistEntry entry = null;
			try {
				entry = manager.get(entryID);
			} catch (SQLException e) {
				LoggingUtil.error(e);
			}
			if (entry != null) {
				MessageEmbed embed = EmbedUtil.WhitelistEmbed(entry);
				assert embed != null;
				LoggingUtil.getLogChannel().sendMessageEmbeds(embed).queue();
				interactionEvent.reply("Whitelist retrieved").setEphemeral(true).queue();
		}
			else {
				interactionEvent.reply("No entry found").setEphemeral(true).queue();
			}
		}
	}
}
