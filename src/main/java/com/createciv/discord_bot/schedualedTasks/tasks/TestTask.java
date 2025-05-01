package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.classes.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class TestTask extends ScheduledTask {

    public TestTask() {
        super("test", TimeUnit.SECONDS, 5);
    }

    @Override
    public void execute() {
        Bot.LOGGER.info("Test Task Fired");
    }
}
