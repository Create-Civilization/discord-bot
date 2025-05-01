package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;

public class MojangAPI {

    /**
     * Get the UUID of a player
     * @param username The username you want to get.
     * @return JsonObject
     */
    public JsonObject getUUID(String username){
        try {
            Gson gson = new Gson();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + username))
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



    public JsonObject getServerStats(String serverIP){
        try{
            Gson gson = new Gson();
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.mcsrvstat.us/3/" +  serverIP))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() == 200){
                return gson.fromJson(response.body(), JsonObject.class);
            }

            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }








}
