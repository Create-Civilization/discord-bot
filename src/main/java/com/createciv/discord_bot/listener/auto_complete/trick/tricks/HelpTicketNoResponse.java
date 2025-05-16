package com.createciv.discord_bot.listener.auto_complete.trick.tricks;

import com.createciv.discord_bot.listener.auto_complete.trick.base.BaseTrick;

public class HelpTicketNoResponse extends BaseTrick {
    public HelpTicketNoResponse() {
        super("Help Ticket No Response", "Why is no one responding to my help ticket?",
                "The moderation team is all volunteers and we dont always have the time to respond to all help tickets immediately. " +
                        "We do our best to respond to tickets as soon as we can but sometimes they expire. If this happens please just make a new ticket and we will do our best to get to it ASAP.");
    }
}
