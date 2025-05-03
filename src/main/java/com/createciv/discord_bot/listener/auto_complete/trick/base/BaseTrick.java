package com.createciv.discord_bot.listener.auto_complete.trick.base;

import com.createciv.discord_bot.Bot;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;

public abstract class BaseTrick {

    private String name;
    private String title;
    private String description;

    public BaseTrick(String name, String title, String description) {
        this.name = name;
        this.title = title;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public MessageEmbed getEmbed() {
        return new EmbedBuilder()
                .setColor(Color.decode("#809ae8"))
                .setTitle(getTitle())
                .setDescription(getDescription())
                .setFooter(Bot.BOT.getName() + " | " + Bot.BOT.getId(), Bot.BOT.getAvatarUrl())
                .build();
    }
}
