package com.createciv.discord_bot.listener.modal;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.util.MojangAPI;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.UUID;

public class WhitelistListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if(event.getModalId().equals("whitelist")){
            String username = event.getValue("username").getAsString();
            String reason = event.getValue("reason").getAsString();
            String referral = event.getValue("referral").getAsString();

            MojangAPI mojangAPI = new MojangAPI();
            JsonObject response = mojangAPI.getUUID(username);
            if(response == null){
                event.reply("A severe error occurred. Please try again later").setEphemeral(true).queue();
                return;
            }
            //Check if we got an invalid username
            if(response.get("errorMessage") != null){
                event.reply(response.get("errorMessage").getAsString()).setEphemeral(true).queue();
                return;
            }

            if(response.get("id").getAsString() != null){
                //Dont ask me how Regex works. I AIed this.
                String formattedUuid = response.get("id").getAsString().replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"
                );
                UUID formatedUUID = UUID.fromString(formattedUuid);

                //Do database stuff here.

                event.reply(formatedUUID.toString()).setEphemeral(true).queue();
                return;
            }

            event.reply("Unknown error occurred, If this continues please contact a developer").setEphemeral(true).queue();
        }
    }
}
