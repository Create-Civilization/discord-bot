package com.createciv.discord_bot.commands.server.moderation;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.SlashCommand;
import com.createciv.discord_bot.listener.auto_complete.moderation.ModerationAutoComplete;
import com.createciv.discord_bot.listener.auto_complete.moderation.times.BaseTime;
import com.createciv.discord_bot.schedualedTasks.tasks.UpdateStatus;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.MojangAPI;
import com.createciv.discord_bot.util.PanelConnection;
import com.createciv.discord_bot.util.database.DatabaseManager;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.ModerationManager;
import com.createciv.discord_bot.util.database.managers.WhitelistManager;
import com.createciv.discord_bot.util.database.types.ModerationEntry;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.Random;

public class TempBanUser extends SlashCommand {

    private static String BannedRoleID = ConfigLoader.BANNED_ROLE_ID;

    public TempBanUser() {
        super("temp_ban_user", "temporarily bans a user from the discord and server");
        addOption(new Option(OptionType.USER, "user", "user to ban", true, false));
        addOption(new Option(OptionType.INTEGER, "length", "how long to ban user", true, false));
        addOption(new Option(OptionType.STRING, "timespan", "timespan to ban", true, true));
        addOption(new Option(OptionType.STRING, "reason", "reason for banning", true, true));

    }

    @Override
    public void execute(SlashCommandInteractionEvent interactionEvent) {

        if (!hasPermission(interactionEvent)) {
            interactionEvent.reply("You do not have permission to run this command").setEphemeral(true).queue();
            return;
        }

        if (BannedRoleID == null) {
            interactionEvent.reply("Banned role is not configured. Please configure it to use this command")
                    .setEphemeral(true).queue();
            return;
        }


        interactionEvent.deferReply(true).queue();

        String reason = "\"" + interactionEvent.getOption("reason").getAsString() + "\"";
        int length = interactionEvent.getOption("length").getAsInt();
        User user = interactionEvent.getOption("user").getAsUser();

        Optional<BaseTime> timespan = Arrays.stream(ModerationAutoComplete.timespans)
                .filter(name -> name.getName().equalsIgnoreCase(interactionEvent.getOption("timespan").getAsString()))
                .findFirst();

        long timestamp = System.currentTimeMillis() / 1000;
        timestamp += (timespan.get().getTimeMultiplier() * length);


        WhitelistEntry entry;
        try {
            entry = DatabaseRegistry.getWhitelistManager().getWithDiscordID(user.getId());
        } catch (Exception e) {
            interactionEvent.getHook().editOriginal("Error getting whitelist entry").queue();
            return;
        }

        if (entry == null) {
            interactionEvent.getHook().editOriginal("User is not whitelisted").queue();
            return;
        }

        ModerationEntry moderationEntry = new ModerationEntry(
                interactionEvent.getMember().getId(),
                entry.discordID,
                entry.playerUUID,
                "BAN",
                interactionEvent.getOption("reason").getAsString(),
                new Timestamp(timestamp * 1000),
                false
        );

        MojangAPI mojangAPI = new MojangAPI();
        JsonObject object = mojangAPI.getPlayerInfo(String.valueOf(entry.playerUUID));
        String username = object.get("username").getAsString();
        Role role = interactionEvent.getGuild().getRoleById(BannedRoleID);
        Guild guild = interactionEvent.getGuild();
        User punishedUser = interactionEvent.getOption("user").getAsUser();

        WhitelistManager whitelistManager = DatabaseRegistry.getWhitelistManager();
        WhitelistEntry whitelistEntry;

        try {
            whitelistEntry = whitelistManager.getWithDiscordID(punishedUser.getId());
        } catch (Exception e) {
            LoggingUtil.logError(e);
            interactionEvent.getHook().editOriginal("Error getting whitelist entry").queue();
            return;
        }

        if (whitelistEntry != null) {
            guild.removeRoleFromMember(punishedUser, guild.getRoleById(ConfigLoader.WHITELIST_ROLE_ID)).queue();
        }

        //Give user role
        guild.addRoleToMember(punishedUser, role).queue();


        ModerationManager manager = DatabaseRegistry.getModerationManager();

        if (UpdateStatus.serverIsLive) {
            PanelConnection.banUser(username, timestamp, reason, getRandomString())
                    .thenAccept(success -> {
                        if (success) {
                            interactionEvent.getHook().editOriginal("User has been banned successfully!").queue();
                            moderationEntry.executedOnServer = true;
                            try {
                                manager.add(moderationEntry);
                            } catch (Exception e) {
                                interactionEvent.getHook().editOriginal("Error adding moderation entry").queue();
                            }
                        } else {
                            interactionEvent.getHook().editOriginal("Command has failed, check server console for more info.").queue();
                        }
                    })
                    .exceptionally(error -> {
                        interactionEvent.getHook().editOriginal("Command timed out, check logs for more info").queue();
                        return null;
                    });
        } else {
            try {
                manager.add(moderationEntry);
            } catch (Exception e) {
                interactionEvent.getHook().editOriginal("Error adding moderation entry").queue();
                return;
            }
        }


    }

    protected String getRandomString() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 18) { // length of the random string.
            int index = (int) (rnd.nextFloat() * characters.length());
            salt.append(characters.charAt(index));
        }
        return salt.toString();

    }

    private boolean hasPermission(SlashCommandInteractionEvent interactionEvent) {

        if (ConfigLoader.ADMIN_ROLE_IDS.isEmpty()) {
            Bot.LOGGER.error("No admin roles selected. Please set this in the config to continue");
            interactionEvent.reply("Admin roles are not configured. Please configure them to use this command")
                    .setEphemeral(true).queue();
            return false;
        }

        if (interactionEvent.getMember() == null) {
            interactionEvent.reply("Could not find member information").setEphemeral(true).queue();
            return false;
        }

        boolean hasAdminRole = interactionEvent.getMember().getRoles().stream()
                .map(Role::getId)
                .anyMatch(ConfigLoader.ADMIN_ROLE_IDS::contains);

        if (!hasAdminRole) {
            interactionEvent.reply("You do not have permission to run this command").setEphemeral(true).queue();
            return false;
        }
        return true;
    }
}
