package com.createciv.discord_bot;

import com.createciv.discord_bot.classes.SlashCommand;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.createciv.discord_bot.ConfigLoader.BOT_TOKEN;

public class Bot extends ListenerAdapter {
    public static final Logger LOGGER = LoggerFactory.getLogger("BOT_LOG");

    public static void main(String[] args){
        LOGGER.info("Initiating bot..");
        JDA api = JDABuilder.createDefault(BOT_TOKEN)
                .addEventListeners(new Bot())
                .build();
    }

    private void registerSlashCommands(ReadyEvent event) {
        CommandListUpdateAction commands = event.getJDA().updateCommands();

        // Use reflection to register all commands
        List<Class<? extends SlashCommand>> subclasses = getSubclasses(SlashCommand.class);
        for (Class<? extends SlashCommand> subclass : subclasses) {
            try {
                SlashCommand commandInstance = subclass.getDeclaredConstructor().newInstance();
                SlashCommand.registerCommand(commandInstance);

                LOGGER.info("Successfully collected slash command: {}", subclass.getSimpleName());

            } catch (Exception e) {
                LOGGER.error("Failed to register slash command: {}", subclass.getSimpleName(), e);
            }
        }

        SlashCommand.COMMAND_REGISTRY.forEach((name, command) -> {
            SlashCommandData commandData = Commands.slash(command.getName(), command.getDescription());
            for (SlashCommand.Option option : command.getOptions()) {
                commandData.addOption(option.getOptionType(),option.getName(),option.getDescription(), option.isRequired());
            }
            commands.addCommands(
                    commandData
            );
            LOGGER.info("Successfully parsed and registered command: {}", name);
        });

        commands.queue();
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.registerSlashCommands(event);
        LOGGER.info("Bot successfully initiated.");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommand command = SlashCommand.COMMAND_REGISTRY.get(event.getName());
        if (command != null) {
            command.execute(event);
        } else {
            event.reply("Unknown command").queue();
        }
    }

    public static List<Class<? extends SlashCommand>> getSubclasses(Class<SlashCommand> abstractClass) {
        List<Class<? extends SlashCommand>> subclasses = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()  // Enable all scanning features
                .scan()) {

            // Scan all classes and find those that extend SlashCommand
            scanResult.getSubclasses(abstractClass.getName()).forEach(classInfo -> {
                try {
                    Class<? extends SlashCommand> cls = (Class<? extends SlashCommand>) Class.forName(classInfo.getName());
                    if (abstractClass.isAssignableFrom(cls)) {
                        subclasses.add(cls);
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            });
        }
        return subclasses;
    }
}
