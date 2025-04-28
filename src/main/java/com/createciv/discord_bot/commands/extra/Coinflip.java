package com.createciv.discord_bot.commands.extra;

import com.createciv.discord_bot.classes.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.Random;

@SuppressWarnings("unused")
public class Coinflip extends SlashCommand {
    private static final Random random = new Random();

    public Coinflip() {
        super("coinflip", "Flip a coin for heads or tails.");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        interactionEvent.reply(random.nextBoolean() ? "Heads!" : "Tails!").queue();
    }
}
