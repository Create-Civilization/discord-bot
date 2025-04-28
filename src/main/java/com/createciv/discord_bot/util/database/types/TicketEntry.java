package com.createciv.discord_bot.util.database.types;

public class TicketEntry {

    private int ID;
    private String authorID;
    private String threadChannelID;
    private String embedMessageID;
    private long lastActive;


    public TicketEntry(int ID, String authorID, String threadChannelID, String embedMessageID, long lastActive){
        this.ID = ID;
        this.authorID = authorID;
        this.threadChannelID = threadChannelID;
        this.embedMessageID = embedMessageID;
        this.lastActive = lastActive;
    }

    public int getID(){
        return ID;
    }

    public String getAuthorID(){
        return authorID;
    }

    public String getThreadChannelID(){
        return threadChannelID;
    }

    public String getEmbedMessageID(){
        return embedMessageID;
    }

    public long getLastActive(){
        return lastActive;
    }

    public void setLastActive(int newActivity){
        lastActive = newActivity;
    }


}
