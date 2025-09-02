package com.createciv.discord_bot.listener.logging;

import com.createciv.discord_bot.util.LoggingUtil;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class JoinAndLeave extends ListenerAdapter {

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
       // new LoggingUtil().logUserJoin(event.getUser());
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
       // new LoggingUtil().logUserRemove(event.getUser());
    }
}
