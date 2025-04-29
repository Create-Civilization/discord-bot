package com.createciv.discord_bot.util;

import com.createciv.discord_bot.ConfigLoader;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;

public class PteroAPI {

    String pannelURL = ConfigLoader.PANEL_URL;
    String pannelToken = ConfigLoader.PETRO_PANEL_TOKEN;

    public void startServer(String serverID){
        PteroClient api = PteroBuilder.createClient(pannelURL, pannelToken);
        api.retrieveServerByIdentifier(serverID)
                .flatMap(ClientServer::start).execute();
    }

}
