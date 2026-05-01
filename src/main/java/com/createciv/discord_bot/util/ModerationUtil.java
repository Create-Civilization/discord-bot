package com.createciv.discord_bot.util;

import com.createciv.discord_bot.Bot;
import com.createciv.discord_bot.ConfigLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.util.List;

public class ModerationUtil {
	public static Boolean isModeratorOnEvent(SlashCommandInteractionEvent interactionEvent){
		Guild guild = interactionEvent.getGuild();
		Member mem = interactionEvent.getMember();
		assert guild != null;
		assert mem != null;
		List<Role> userRoles = mem.getRoles();
		for (String adminRole : ConfigLoader.ADMIN_ROLE_IDS){
			if (userRoles.contains(guild.getRoleById(adminRole))){
				return true;
			}}
		return false;
	}
}
