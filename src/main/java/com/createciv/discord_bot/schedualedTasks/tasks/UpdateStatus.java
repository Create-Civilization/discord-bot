package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.util.MojangAPI;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;

import java.util.concurrent.TimeUnit;

public class UpdateStatus extends ScheduledTask {

    public UpdateStatus() {
        super("update_bot_status", TimeUnit.SECONDS, 30);
    }

    @Override
    public void execute() {

        JsonObject serverInfo = new MojangAPI().getServerStats(ConfigLoader.SERVER_IP);

        if (serverInfo == null) {
            updateBotStatus("Server is offline", false);
        } else {
            JsonObject playerInfo = serverInfo.get("players").getAsJsonObject();
            int playerCount = playerInfo.get("online").getAsInt();
            int maxPlayerCount = playerInfo.get("max").getAsInt();
            updateBotStatus(playerCount + "/" + maxPlayerCount + " players", true);
        }

    }

    private void updateBotStatus(String status, boolean serverIsLive) {
        JDA jda = Bot.API;

        if (!serverIsLive) {
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.customStatus(status));
            return;
        }

        jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.watching(status));
    }
}
