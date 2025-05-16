package com.createciv.discord_bot.classes;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public abstract class ScheduledTask {

    String name;
    TimeUnit timeUnit;
    int interval;

    public ScheduledTask(String name, TimeUnit timeUnit, int interval){
        this.name = name;
        this.timeUnit = timeUnit;
        this.interval = interval;
    }

    public static final Map<String, ScheduledTask> REGISTRY = new HashMap<>();

    public abstract void execute();

    public static void register(ScheduledTask task) {
        REGISTRY.put(task.getName(), task);
    }

    public String getName(){
        return this.name;
    }

    public TimeUnit getTimeUnit(){
        return this.timeUnit;
    }

    public int getInterval(){
        return this.interval;
    }

}
