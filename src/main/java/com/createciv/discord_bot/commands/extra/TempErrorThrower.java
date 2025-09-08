package com.createciv.discord_bot.commands.extra;

import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.util.LoggingUtil;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.logging.Logger;

public class TempErrorThrower extends SlashCommand {
    public TempErrorThrower() {
        super("throw_error", "me when error");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        try {
            throw new RuntimeException("Something went wrong");
        } catch (Exception e) {
            LoggingUtil.error(e);
        }
    }
}
