package com.createciv.discord_bot.listener.modal;

import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.ModerationUtil;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import static com.createciv.discord_bot.Bot.LOGGER;

public class WhitelistListener extends ListenerAdapter {

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        try {
            if (event.getModalId().equals("whitelist")) {
                String username = Objects.requireNonNull(event.getValue("username")).getAsString();
                String reason = Objects.requireNonNull(event.getValue("reason")).getAsString();
                String referral = Objects.requireNonNull(event.getValue("referral")).getAsString();

                MojangAPI mojangAPI = new MojangAPI();
                JsonObject response = mojangAPI.getUUID(username);
                if (response == null) {
                    event.reply("A severe error occurred. Please try again later").setEphemeral(true).queue();
                    return;
                }
                //Check if we got an invalid username
                if (response.get("errorMessage") != null) {
                    event.reply(response.get("errorMessage").getAsString()).setEphemeral(true).queue();
                    return;
                }

                if (response.get("id").getAsString() != null) {
                    //Dont ask me how Regex works. I AIed this.
                    String formattedUuid = response.get("id").getAsString().replaceFirst(
                            "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                            "$1-$2-$3-$4-$5"
                    );
                    UUID formatedUUID = UUID.fromString(formattedUuid);
                    WhitelistEntry entry = new WhitelistEntry(formatedUUID, event.getUser().getId(), username, reason, referral);

                    try {
                        DatabaseRegistry.getWhitelistManager().add(entry);
                    } catch (SQLException e) {
                        LOGGER.error("An error occurred when whitelisting", e);
                        throw new RuntimeException(e);
                    }

                    event.reply("You have been successfully whitelisted").setEphemeral(true).queue();
                    new LoggingUtil().logWhitelists(entry, event.getUser());
                    return;
                }

                event.reply("Unknown error occurred, If this continues please contact a developer").setEphemeral(true).queue();
            }
        } catch (Exception e) {
            new LoggingUtil().logError(e);
        }
    }
}
