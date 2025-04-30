package com.createciv.discord_bot.util;

import com.createciv.discord_bot.ConfigLoader;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.ClientServer;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;

public class PteroAPI {

    String panelURL = ConfigLoader.PANEL_URL;
    String panelToken = ConfigLoader.PETRO_PANEL_TOKEN;

    public void startServer(String serverID){
        PteroClient api = PteroBuilder.createClient(panelURL, panelToken);
        api.retrieveServerByIdentifier(serverID)
                .flatMap(ClientServer::start).execute();
    }

}
