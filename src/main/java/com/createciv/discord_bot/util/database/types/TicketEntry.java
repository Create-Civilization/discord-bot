package com.createciv.discord_bot.util.database.types;

import com.createciv.discord_bot.util.database.TableEntry;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.UUID;

public class TicketEntry extends TableEntry<TicketEntry> {

	public int id;
	public String authorID;
	public String threadChannelID;
	public String embedMessageID;
	public Timestamp lastActivity;
	public Boolean active;

	public TicketEntry(ResultSet resultSet) throws SQLException {
		id = resultSet.getInt("id");
		authorID = resultSet.getString("authorID");
		threadChannelID = resultSet.getString("threadChannelID");
		embedMessageID = resultSet.getString("embedMessageID");
		lastActivity = resultSet.getTimestamp("lastActivity");
		active = resultSet.getBoolean("active");
	}
	public TicketEntry(Builder builder){
		embedMessageID = builder.embedMessageID;
		threadChannelID= builder.threadChannelID;
		authorID = builder.authorID;
		lastActivity = builder.lastActivity;
		active = builder.active;
		id = builder.id;
	}


	public static class Builder {
		private Boolean active;
		private String authorID;
		private Integer id;
		private String threadChannelID;
		private String embedMessageID;
		private Timestamp lastActivity;

		public Builder active(Boolean active) {
			this.active = active;
			return this;
		}

		public Builder lastActivity(Timestamp lastActivity) {
			this.lastActivity = lastActivity;
			return this;
		}

		public Builder threadChannelID(String threadChannelID) {
			this.threadChannelID = threadChannelID;
			return this;
		}

		public Builder embedMessageID(String embedMessageID) {
			this.embedMessageID = embedMessageID;
			return this;
		}
		public Builder authorID(String authorID) {
			this.authorID = authorID;
			return this;
		}
		public TicketEntry build(){
			if (authorID == null) throw new IllegalStateException("authorID is null");
			if (threadChannelID == null || embedMessageID == null) throw new IllegalStateException("either embed msg id or thread channel id is null");
			return new TicketEntry(this);
		}
	}


}