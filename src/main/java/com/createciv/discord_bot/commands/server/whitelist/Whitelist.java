package com.createciv.discord_bot.commands.server.whitelist;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.modals.Modal;

import java.sql.SQLException;

@SuppressWarnings("unused")
public class Whitelist extends SlashCommand {

	public Whitelist() {
		super("whitelist", "Add or remove a user from the whitelist");
	}

	@Override
	public void execute(SlashCommandInteractionEvent interactionEvent) {
		if (!Bot.DB_HEALTHY) {
			interactionEvent.reply("Database is not connected, try again later").queue();
			return;
		}

		if (ConfigLoader.WHITELIST_ROLE_ID == null) {
			interactionEvent
				.reply("Whitelist role is not configured. Please configure it to use this command")
				.setEphemeral(true)
				.queue();
			return;
		}

		WhitelistTable manager = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");
		WhitelistEntry entry;
		try {
			entry = manager.get(interactionEvent.getUser().getId());
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}

		if (entry != null) {
			interactionEvent.reply("You are already whitelisted.").setEphemeral(true).queue();
			return;
		}

		TextInput username = TextInput.create("username", TextInputStyle.SHORT)
			.setPlaceholder("Type your username here")
			.setRequired(true)
			.setMinLength(3)
			.setMaxLength(16)
			.build();
		TextInput referral = TextInput.create("referral", TextInputStyle.PARAGRAPH)
			.setPlaceholder("Examples: S1 Player, X user told me, Reddit, etc")
			.setRequired(true)
			.setMinLength(3)
			.setMaxLength(800)
			.build();

		Modal modal = Modal.create("whitelist", "Whitelist")
			.addComponents(Label.of("Subject", username), Label.of("Body", referral))
			.build();

		interactionEvent.replyModal(modal).queue();
	}
}