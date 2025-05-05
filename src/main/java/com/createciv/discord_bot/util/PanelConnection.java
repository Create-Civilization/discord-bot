package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;
import com.mattmalec.pterodactyl4j.client.managers.WebSocketBuilder;
import com.mattmalec.pterodactyl4j.client.managers.WebSocketManager;
import com.mattmalec.pterodactyl4j.client.ws.events.AuthSuccessEvent;
import com.mattmalec.pterodactyl4j.client.ws.events.output.ConsoleOutputEvent;
import com.mattmalec.pterodactyl4j.client.ws.events.output.OutputEvent;
import com.mattmalec.pterodactyl4j.client.ws.hooks.ClientSocketListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PanelConnection extends ClientSocketListenerAdapter {

    private static String panelURL = ConfigLoader.PANEL_URL;
    private static String token = ConfigLoader.PETRO_PANEL_TOKEN;
    private static String serverID = ConfigLoader.SERVER_ID;
    private static PteroClient client;

    private static final Map<String, CompletableFuture<Boolean>> trackerFutures = new ConcurrentHashMap<>();

    public static void init() {
        client = PteroBuilder.createClient(panelURL, token);
        client.retrieveServerByIdentifier(serverID)
                .map(ClientServer::getWebSocketBuilder)
                .map(builder -> builder.addEventListeners(new PanelConnection()))
                .executeAsync(WebSocketBuilder::build);
    }


    @Override
    public void onAuthSuccess(AuthSuccessEvent event) {
    }


    @Override
    public void onOutput(OutputEvent event) {
        List<String> lines = Arrays.asList(event.getLine().split("\n"));

        for (String line : lines) {
            for (String tracker : trackerFutures.keySet()) {
                if (line.contains("trackerID: " + tracker)) {
                    CompletableFuture<Boolean> future = trackerFutures.get(tracker);
                    if (future != null && !future.isDone()) {
                        boolean success = !line.contains("COMMAND FAILED");
                        future.complete(success);
                    }
                    trackerFutures.remove(tracker);
                }
            }
        }
    }

    public static CompletableFuture<Boolean> banUser(String username, Long duration, String reason, String trackerID) {
        Bot.LOGGER.info("Banning user: " + username);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        String command = "civutil tempban " + username + " " + duration + " " + reason + " " + trackerID;
        trackerFutures.put(trackerID, future);
        client.retrieveServerByIdentifier(serverID)
                .flatMap(server -> server.sendCommand(command))
                .executeAsync();
        scheduleFutureTimeout(future, trackerID, 30);
        return future;
    }

    private static void scheduleFutureTimeout(CompletableFuture<Boolean> future, String trackerID, int timeoutSeconds) {
        CompletableFuture.delayedExecutor(timeoutSeconds, TimeUnit.SECONDS).execute(() -> {
            if (!future.isDone()) {
                trackerFutures.remove(trackerID);
                future.completeExceptionally(new TimeoutException("No response received for tracker: " + trackerID));
            }
        });
    }
}
