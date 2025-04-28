package com.createciv.discord_bot.classes;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public abstract class SlashCommand {
    private String name;
    private String description;
    private final List<Option> options = new ArrayList<>();

    public static final Map<String, SlashCommand> COMMAND_REGISTRY = new HashMap<>();

    public SlashCommand(String name, String description) {
        this.name = name;
        this.description = description;
        registerCommand(this);
    }

    public abstract void execute(SlashCommandInteractionEvent interactionEvent);

    public static void registerCommand(SlashCommand command) {
        COMMAND_REGISTRY.put(command.getName(), command);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void addOption(Option option) {
        this.options.add(option);
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static class Option {
        private final OptionType optionType;
        private final String name;
        private final String description;
        private final boolean required;

        public Option(OptionType optionType, String name, String description, boolean required) {
            this.optionType = optionType;
            this.name = name;
            this.description = description;
            this.required = required;
        }

        public OptionType getOptionType() {
            return optionType;
        }

        public String getName() {
            return name;
        }

        public String getDescription() {
            return description;
        }

        public boolean isRequired() {
            return required;
        }
    }
}
