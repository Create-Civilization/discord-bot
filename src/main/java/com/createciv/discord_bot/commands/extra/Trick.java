package com.createciv.discord_bot.commands.extra;

import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.listener.auto_complete.trick.TrickAutoComplete;
import com.createciv.discord_bot.listener.auto_complete.trick.base.BaseTrick;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class Trick extends SlashCommand {

    public Trick() {
        super("trick", "Some helpful text based tricks");
        addOption(new Option(OptionType.STRING, "name", "Select the trick to use", true, true));
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        String trickName = Objects.requireNonNull(interactionEvent.getOption("name")).getAsString();

        Optional<BaseTrick> trick = Arrays.stream(TrickAutoComplete.tricks)
                .filter(name -> name.getName().equalsIgnoreCase(trickName))
                .findFirst();


        if (trick.isEmpty()) {
            interactionEvent.reply("Invalid trick name").setEphemeral(true).queue();
            return;
        }
        interactionEvent.replyEmbeds(trick.get().getEmbed()).queue();

    }


}
