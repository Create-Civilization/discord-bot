package com.createciv.discord_bot.commands.extra;

import com.createciv.discord_bot.classes.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class GuessThatNumber extends SlashCommand {

    public GuessThatNumber() {

        super("guessthatnumber", "Guess a number between 1 and 10");
        addOption(new Option(OptionType.STRING,"number","select a number 1-10",true,false));
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        int randNum = (int) (Math.random() * (10) + 1);
        String number = Objects.requireNonNull(interactionEvent.getOption("number")).getAsString();
        boolean isNumeric = number.codePoints().allMatch( Character::isDigit );
        if (number.isBlank() || !isNumeric ){
            interactionEvent.reply("Invalid Input").queue();
        }
        else if (randNum == Integer.parseInt(number)) {
            interactionEvent.reply("CORRECT! The number was " + randNum).queue();
        }
        else {
            interactionEvent.reply("WRONG! The number was " + randNum +"." + " You guessed " + number).queue();
        }
    }
}
