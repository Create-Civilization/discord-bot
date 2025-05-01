package com.createciv.discord_bot.schedualedTasks;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.classes.ScheduledTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TaskRegistry {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    public static void init(){
        for (ScheduledTask task : ScheduledTask.REGISTRY.values()){
            scheduler.scheduleAtFixedRate(
                    task::execute,
                    0,
                    task.getInterval(),
                    task.getTimeUnit()
            );
            Bot.LOGGER.info("Registered Task: {}", task.getName());
        }
    }

    public static void shutdown(){
        scheduler.shutdown();
    }

}
