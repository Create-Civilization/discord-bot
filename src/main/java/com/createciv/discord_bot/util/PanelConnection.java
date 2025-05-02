package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.mattmalec.pterodactyl4j.PteroBuilder;
import com.mattmalec.pterodactyl4j.client.entities.PteroClient;

public class PanelConnection {

    private static String panelURL = ConfigLoader.PANEL_URL;
    private static String token = ConfigLoader.PETRO_PANEL_TOKEN;
    private static PteroClient client;

    public static void init() {
        client = PteroBuilder.createClient(panelURL, token);
    }


    /**
     * Retrieves the static instance of the PteroClient that is used to interact with the panel.
     *
     * @return The singleton instance of the PteroClient, or null if it has not been initialized.
     */
    public static PteroClient getClient() {
        return client;
    }

    /**
     * Sends a command to a specified server through the PteroClient.
     * If the Panel connection is not initialized, it logs an error and terminates the operation.
     * Otherwise, retrieves the server by its identifier and sends the command asynchronously.
     * Logs success or error messages based on the process outcome.
     *
     * @param command  The command to be sent to the server.
     * @param serverID The identifier of the server to which the command will be sent.
     */
    public static void sendCommandToServer(String command, String serverID) {
        if (client == null) {
            Bot.LOGGER.error("Panel connection is not initialized!");
            return;
        }

        Bot.LOGGER.info("Sending command '{}' to server: {}", command, serverID);

        client.retrieveServerByIdentifier(serverID).executeAsync(server -> {
            server.sendCommand(command)
                    .executeAsync(
                            success -> {
                                Bot.LOGGER.info("Command '{}' successfully sent to server {}", command, serverID);
                            },
                            error -> {
                                Bot.LOGGER.error("Error sending command to server: {}", error.getMessage());
                            }
                    );
        }, error -> {
            Bot.LOGGER.error("Error retrieving server {}: {}", serverID, error.getMessage());
        });
    }


}
