package com.createciv.discord_bot;

import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.listener.auto_complete.trick.TrickAutoComplete;
import com.createciv.discord_bot.listener.logging.JoinAndLeave;
import com.createciv.discord_bot.listener.message.TicketCreator;
import com.createciv.discord_bot.listener.modal.WhitelistListener;
import com.createciv.discord_bot.schedualedTasks.TaskRegistry;
import com.createciv.discord_bot.util.PanelConnection;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.SelfUser;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.CommandListUpdateAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.ArrayList;
import java.util.List;

import static com.createciv.discord_bot.ConfigLoader.BOT_TOKEN;

public class Bot extends ListenerAdapter {
    public static final Logger LOGGER = LoggerFactory.getLogger("BOT_LOG");
    public static JDA API;
    public static SelfUser BOT;

    // Markers
    private static final Marker REGISTRATION_MARKER = MarkerFactory.getMarker("REGISTRATION");

    public static void main(String[] args){
        LOGGER.info("Initiating bot..");

        DatabaseRegistry.init();
        PanelConnection.init();

        API = JDABuilder.createDefault(BOT_TOKEN)
                .addEventListeners(new Bot())
                .addEventListeners(new WhitelistListener())
                .addEventListeners(new TicketCreator())
                .addEventListeners(new JoinAndLeave())
                .addEventListeners(new TrickAutoComplete())
                .build();

        BOT = API.getSelfUser();
    }

    @Override
    public void onReady(ReadyEvent event) {
        this.registerSlashCommands(event);
        this.registerModals(event);
        this.registerScheduledTasks();
        TaskRegistry.init();
        LOGGER.info("Bot successfully initiated.");
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        SlashCommand command = SlashCommand.REGISTRY.get(event.getName());
        if (command != null) {
            command.execute(event);
        } else {
            event.reply("Unknown command").queue();
        }
    }

    //Check if the databases exist

    private void registerSlashCommands(ReadyEvent readyEvent) {
        LOGGER.info(REGISTRATION_MARKER, "Registering commands..");
        CommandListUpdateAction commands = readyEvent.getJDA().updateCommands();

        // Use reflection to register all commands
        List<Class<? extends SlashCommand>> subclasses = getSubclasses(SlashCommand.class);
        for (Class<? extends SlashCommand> subclass : subclasses) {
            try {
                SlashCommand commandInstance = subclass.getDeclaredConstructor().newInstance();
                SlashCommand.register(commandInstance);

                LOGGER.info("Successfully collected slash command: {}", subclass.getSimpleName());

            } catch (Exception e) {
                LOGGER.error("Failed to register slash command: {}", subclass.getSimpleName(), e);
            }
        }

        SlashCommand.REGISTRY.forEach((name, command) -> {
            SlashCommandData commandData = Commands.slash(command.getIdentifier(), command.getDescription());
            for (SlashCommand.Option option : command.getOptions()) {
                commandData.addOption(option.optionType(), option.name(), option.description(), option.required(), option.autocomplete());
            }
            commands.addCommands(
                    commandData
            );
            LOGGER.info("Successfully parsed and registered command: {}", name);
        });

        commands.queue();

        LOGGER.info(REGISTRATION_MARKER, "Commands registered successfully.");
    }

    private void registerScheduledTasks() {
        LOGGER.info(REGISTRATION_MARKER, "Registering scheduled tasks...");

        List<Class<? extends ScheduledTask>> subclasses = getSubclasses(ScheduledTask.class);
        for (Class<? extends ScheduledTask> subclass : subclasses) {
            try {
                ScheduledTask taskInstance = subclass.getDeclaredConstructor().newInstance();
                ScheduledTask.register(taskInstance);
                LOGGER.info("Successfully registered task: {}", subclass.getSimpleName());
            } catch (Exception e) {
                LOGGER.error("Failed to register scheduled task: {}", subclass.getSimpleName(), e);
            }
        }

        LOGGER.info(REGISTRATION_MARKER, "Scheduled tasks registered successfully.");
    }


    private void registerModals(ReadyEvent readyEvent) {
        LOGGER.info(REGISTRATION_MARKER, "Registering modals..");



        LOGGER.info(REGISTRATION_MARKER, "Modals registered successfully.");
    }

    public static <T> List<Class<? extends T>> getSubclasses(Class<T> abstractClass) {
        List<Class<? extends T>> subclasses = new ArrayList<>();
        try (ScanResult scanResult = new ClassGraph()
                .enableAllInfo()  // Enable all scanning features
                .scan()) {

            // Scan all classes and find those that extend T
            scanResult.getSubclasses(abstractClass.getName()).forEach(classInfo -> {
                try {
                    Class<? extends T> cls = (Class<? extends T>) Class.forName(classInfo.getName());
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
