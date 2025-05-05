package com.createciv.discord_bot.listener.auto_complete.moderation.times;

public class BaseTime {

    long timeMultiplier;
    String name;

    public BaseTime(long timeMultiplier, String name) {
        this.timeMultiplier = timeMultiplier;
        this.name = name;
    }

    public long getTimeMultiplier() {
        return timeMultiplier;
    }

    public String getName() {
        return name;
    }

}
