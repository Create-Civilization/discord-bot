package com.createciv.discord_bot.listener.auto_complete.trick;

import com.createciv.discord_bot.listener.auto_complete.trick.base.BaseTrick;
import com.createciv.discord_bot.listener.auto_complete.trick.tricks.HelpTicketNoResponse;
import com.createciv.discord_bot.listener.auto_complete.trick.tricks.HowWhitelist;
import com.createciv.discord_bot.listener.auto_complete.trick.tricks.Season2ETA;
import com.createciv.discord_bot.listener.auto_complete.trick.tricks.WorldMap;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Stream;

public class TrickAutoComplete extends ListenerAdapter {

    public static final BaseTrick[] tricks = {
            new HelpTicketNoResponse(),
            new HowWhitelist(),
            new WorldMap(),
            new Season2ETA()};

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("trick") && event.getFocusedOption().getName().equals("name")) {
            List<Command.Choice> options = Stream.of(tricks)
                    .map(word -> new Command.Choice(word.getName(), word.getName()))
                    .toList();

            event.replyChoices(options).queue();
        }
    }
}
