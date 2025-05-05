package com.createciv.discord_bot.schedualedTasks.tasks;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import com.createciv.discord_bot.classes.ScheduledTask;
import com.createciv.discord_bot.util.LoggingUtil;
import com.createciv.discord_bot.util.database.DatabaseManager;
import com.createciv.discord_bot.util.database.DatabaseRegistry;
import com.createciv.discord_bot.util.database.managers.ModerationManager;
import com.createciv.discord_bot.util.database.managers.WhitelistManager;
import com.createciv.discord_bot.util.database.types.ModerationEntry;
import com.createciv.discord_bot.util.database.types.TicketEntry;
import com.createciv.discord_bot.util.database.types.WhitelistEntry;
import com.mattmalec.pterodactyl4j.entities.User;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class CheckExpiredPunishments extends ScheduledTask {
    public CheckExpiredPunishments() {
        super("CheckExpiredPunishments", TimeUnit.MINUTES, 5);
    }

    @Override
    public void execute() {

        ModerationManager manager = DatabaseRegistry.getModerationManager();
        WhitelistManager whitelistManager = DatabaseRegistry.getWhitelistManager();

        List<ModerationEntry> moderationEntries;
        try {
            moderationEntries = manager.getExpiredPunishments();
        } catch (Exception e) {
            LoggingUtil.logError(e);
            throw new RuntimeException(e);
        }

        if (moderationEntries.isEmpty()) {
            return;
        }
        JDA jda = Bot.API;
        Guild guild = jda.getGuildById(ConfigLoader.GUILD_ID);
        Role bannedRole = guild.getRoleById(ConfigLoader.BANNED_ROLE_ID);
        Role whitelistRole = guild.getRoleById(ConfigLoader.WHITELIST_ROLE_ID);
        for (ModerationEntry entry : moderationEntries) {
            Member punishedUser = guild.getMemberById(entry.playerID);
            if (punishedUser == null) {
                //Assume they left the server
                continue;
            }
            guild.removeRoleFromMember(punishedUser, bannedRole).queue();

            WhitelistEntry whitelistEntry;
            try {
                whitelistEntry = whitelistManager.getWithDiscordID(entry.playerID);
            } catch (Exception e) {
                LoggingUtil.logError(e);
                Bot.LOGGER.error("Failed to get whitelist entry for player {}", entry.playerID);
                return;
            }
            if (whitelistEntry != null) {
                guild.addRoleToMember(punishedUser, whitelistRole).queue();
            }

            try {
                manager.removeWithDiscordID(entry.playerID);
            } catch (Exception e) {
                LoggingUtil.logError(e);
                Bot.LOGGER.error("Failed to remove punishment for player {}", entry.playerID);
                return;
            }
        }

    }
}
