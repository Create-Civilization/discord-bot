package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MojangAPI {

    public JsonObject getPlayerInfo(String usernameOrUUID) {
        try {
            Gson gson = new Gson();
            HttpClient client = HttpClient.newHttpClient();
            //https://api.mojang.com/users/profiles/minecraft/
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.ashcon.app/mojang/v2/user/" + usernameOrUUID ))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200 || response.statusCode() == 404){
                return gson.fromJson(response.body(), JsonObject.class);
            } else {
                Bot.LOGGER.error("getUUID failed. Status code {}", response.statusCode());
                return null;
            }
        } catch (Exception e){
            return null;
        }
    }



    /**
     * Retrieves server statistics for the given Minecraft server IP address.
     *
     * @param serverIP The IP address of the Minecraft server for which statistics are to be retrieved.
     * @return A JsonObject containing server statistics if the server response is successful (HTTP status code 200),
     * otherwise null.
     * @throws RuntimeException If an error occurs during the HTTP request process.
     */
    public JsonObject getServerStats(String serverIP, String port) {
        try{
            Gson gson = new Gson();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mcsrvstat.us/3/" + serverIP + ":" + port))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return gson.fromJson(response.body(), JsonObject.class);
            }

            return null;
        } catch (Exception e) {
         //   new LoggingUtil().logError(e);
        }
        return null;
    }


}
