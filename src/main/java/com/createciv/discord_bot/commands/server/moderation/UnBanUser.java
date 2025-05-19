package com.createciv.discord_bot.commands.server.moderation;

import com.createciv.discord_bot.classes.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

public class UnBanUser extends SlashCommand {

    public UnBanUser() {
        super("unban_user", "Unban a user");
        addOption(new Option(OptionType.USER, "user", "The user to unban", true, false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {

    }
}
