package com.createciv.discord_bot.commands.server_commands;

import com.createciv.discord_bot.classes.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

public class Whitelist extends SlashCommand {


    public Whitelist() {
        super("whitelist", "Add or remove a user from the whitelist");
    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {
        TextInput username = TextInput.create("username", "Minecraft Username", TextInputStyle.SHORT)
                .setPlaceholder("Type your username here")
                .setRequired(true)
                .setMinLength(3)
                .setMaxLength(16)
                .build();

        //Switching this to how they found us / why they wanted to join.
        TextInput reason = TextInput.create("reason", "Why would you like to join?", TextInputStyle.PARAGRAPH)
                .setRequired(true)
                .setPlaceholder("Tell us why you wanted to join")
                .build();

        TextInput referral = TextInput.create("referral", "How did you find us?", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setPlaceholder("Please tell us where you found out about Create: Civilization")
                .build();


        Modal modal = Modal.create("whitelist", "Whitelist")
                .addComponents(ActionRow.of(username), ActionRow.of(reason),ActionRow.of(referral))
                .build();

        interactionEvent.replyModal(modal).queue();
    }
}
