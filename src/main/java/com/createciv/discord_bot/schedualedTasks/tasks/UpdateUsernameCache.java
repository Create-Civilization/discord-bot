package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.classes.ScheduledTask;

import java.util.concurrent.TimeUnit;

public class UpdateUsernameCache extends ScheduledTask {

    public UpdateUsernameCache(String name, TimeUnit timeUnit, int interval) {
        super(name, timeUnit, interval);
    }

    @Override
    public void execute() {

    }
}
