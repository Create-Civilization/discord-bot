package com.createciv.discord_bot.listener.modal;

import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.UsernameCacheTable;
import com.createciv.discord_bot.util.database.managers.WhitelistTable;
import com.createciv.discord_bot.util.database.types.UsernameCacheEntry;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

import static com.createciv.discord_bot.Bot.LOGGER;

public class WhitelistListener extends ListenerAdapter {


    //TODO change players nicknames on the server to MC name after the whitelist goes through


    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        try {
            if (event.getModalId().equals("whitelist")) {
                String username = Objects.requireNonNull(event.getValue("username")).getAsString();

                JsonObject response = MojangAPI.getPlayerInfo(username);
                if (response == null) {
                    event.reply("A severe error occurred. Please try again later").setEphemeral(true).queue();
                    return;
                }
                //Check if we got an invalid username
                if (response.get("reason") != null) {
                    event.reply(response.get("reason").getAsString()).setEphemeral(true).queue();
                    return;
                }

                if (response.get("uuid").getAsString() != null) {

                    UUID formatedUUID = UUID.fromString(response.get("uuid").getAsString());
                    WhitelistEntry entry = new WhitelistEntry(formatedUUID, event.getUser().getId());
                    UsernameCacheEntry cacheEntry = new UsernameCacheEntry(response.get("username").getAsString(), formatedUUID);

                    try {
                        WhitelistTable manager = (WhitelistTable) DatabaseRegistry.getTableManager("whitelist");
                        UsernameCacheTable cacheManager = (UsernameCacheTable) DatabaseRegistry.getTableManager("usernameCache");
                        manager.add(entry);
                        cacheManager.add(cacheEntry);
                    } catch (SQLException e) {
                        LOGGER.error("An error occurred when whitelisting", e);
                        throw new RuntimeException(e);
                    }

                    Guild guild = event.getGuild();

                    guild.addRoleToMember(guild.getMember(event.getUser()), guild.getRoleById(ConfigLoader.WHITELIST_ROLE_ID)).queue();

                    event.reply("You have been successfully whitelisted").setEphemeral(true).queue();
                    //@TODO Fix Logging
                    //new LoggingUtil().logWhitelists(entry, event.getUser());
                    return;
                }

                event.reply("Unknown error occurred, If this continues please contact a developer").setEphemeral(true).queue();
            }
        } catch (Exception e) {
         //   new LoggingUtil().logError(e);
        }
    }
}
