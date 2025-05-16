package com.createciv.discord_bot.listener.auto_complete.moderation;

import com.createciv.discord_bot.listener.auto_complete.moderation.times.*;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class ModerationAutoComplete extends ListenerAdapter {

    public static final BaseTime[] timespans = {
            new Hours(),
            new Days(),
            new Months(),
            new Years()
    };

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        if (event.getName().equals("temp_ban_user") && event.getFocusedOption().getName().equals("timespan")) {
            List<Command.Choice> options = Stream.of(timespans)
                    .map(timespan -> new Command.Choice(timespan.getName(), timespan.getName()))
                    .toList();

            event.replyChoices(options).queue();
        }
    }
}
