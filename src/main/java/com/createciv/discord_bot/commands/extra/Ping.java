package com.createciv.discord_bot.commands.extra;

import com.createciv.discord_bot.classes.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

@SuppressWarnings("unused")
public class Ping extends SlashCommand {

    public Ping() {
        super("ping", "Responds with pong!");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        interactionEvent.reply("pong!").queue();
    }
}
