package com.createciv.discord_bot.util.database.types;

import com.google.gson.JsonObject;

import java.sql.Timestamp;

public class PunishmentEntry {
    private String adminDiscordID;
    private String punishmentType;
    private String reason;
    private Timestamp creationTimestamp;
    private Timestamp expirationTimestamp;


    public PunishmentEntry(String adminDiscordID, String punishmentType, String reason, Timestamp creationTimestamp, Timestamp expirationTimestamp) {
        this.adminDiscordID = adminDiscordID;
        this.punishmentType = punishmentType;
        this.reason = reason;
        this.creationTimestamp = creationTimestamp;
        this.expirationTimestamp = expirationTimestamp;
    }

    public PunishmentEntry(JsonObject punishment) {
        //Convert Timestamps
        long creationLong = punishment.get("creationTimestamp").getAsLong();
        Timestamp creationTimestamp = new Timestamp(creationLong);

        long expirationLong = punishment.get("expirationTimestamp").getAsLong();
        Timestamp expirationTimestamp = new Timestamp(expirationLong);

        this.adminDiscordID = punishment.get("adminDiscordID").getAsString();
        this.punishmentType = punishment.get("punishment_type").getAsString();
        this.reason = punishment.get("reason").getAsString();
        this.creationTimestamp = creationTimestamp;
        this.expirationTimestamp = expirationTimestamp;
    }

    public String toString() {
        return toJson().toString();
    }

    public JsonObject toJson() {
        JsonObject punishment = new JsonObject();
        punishment.addProperty("adminDiscordID", adminDiscordID);
        punishment.addProperty("punishment_type", punishmentType);
        punishment.addProperty("reason", reason);
        punishment.addProperty("creationTimestamp", creationTimestamp.getTime());
        punishment.addProperty("expirationTimestamp", expirationTimestamp.getTime());

        return punishment;
    }

    //Getters And Setters
    public String getAdminDiscordID() {
        return adminDiscordID;
    }

    public String getPunishmentType() {
        return punishmentType;
    }

    public String getReason() {
        return reason;
    }

    public Timestamp getCreationTimestamp() {
        return creationTimestamp;
    }

    public Timestamp getExpirationTimestamp() {
        return expirationTimestamp;
    }
}
