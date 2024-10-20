const Database = require('better-sqlite3');
const fs = require('fs');
const path = require('path');

const dbPath = './whitelistData.db';
let db;

function initWhiteListDatabase() {
  const dbExists = fs.existsSync(dbPath);

  if (!dbExists) {
    console.log("Database doesn't exist. Creating new database...");


    db = new Database(dbPath);

    db.prepare(`
        CREATE TABLE IF NOT EXISTS whitelistData (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          playerUUID TEXT NOT NULL UNIQUE,  -- The UUID of the Minecraft player, must be unique
          discordID TEXT NOT NULL UNIQUE,  -- The Discord ID of the player, must be unique
          username TEXT NOT NULL,
          reason TEXT NOT NULL,
          createdAt INTEGER DEFAULT (strftime('%s', 'now'))  -- Time when the record was created
        )
    `).run();
    

    console.log('Database created and table "whitelistData" initialized.');
  } else {
    console.log('Database exists. Opening the database...');
    db = new Database(dbPath); 
  }

  return db;
}


function addUserToWhitelist(playerUUID, discordID, username, reason) {
  return new Promise((resolve, reject) => {
      if (!playerUUID || !discordID || !username || !reason) {
          return reject(new Error("Invalid parameters: playerUUID, discordID, and username are required."));
      }

      const query = `INSERT INTO whitelistData (playerUUID, discordID, username, reason) VALUES (?, ?, ?, ?)`;
      try {
          const stmt = db.prepare(query);
          const info = stmt.run(playerUUID, discordID, username, reason);
          resolve(info.lastInsertRowid);
      } catch (err) {
          console.error("Error inserting into whitelistData:", err); // Log the error for debugging
          reject(err);
      }
  });
}


  function getUserByUUID(playerUUID) {
    return new Promise((resolve, reject) => {
      const query = `SELECT * FROM whitelistData WHERE playerUUID = ?`;
      try {
        const row = db.prepare(query).get(playerUUID);
        resolve(row);
      } catch (err) {
        reject(err);
      }
    });
  }
  
  // Function to get user data by Discord ID
  function getUserByDiscordID(discordID) {
    return new Promise((resolve, reject) => {
      const query = `SELECT * FROM whitelistData WHERE discordID = ?`;
      try {
        const row = db.prepare(query).get(discordID);
        resolve(row);
      } catch (err) {
        reject(err);
      }
    });
  }



  function deleteEntryByUserID(discordID) {
    return new Promise((resolve, reject) => {
      const query = `DELETE FROM whitelistData WHERE discordID = ?`;
      try {
        const stmt = db.prepare(query);
        const info = stmt.run(discordID);
        if (info.changes > 0) {
          resolve(info.changes);
        } else {
          resolve(0);
        }
      } catch (err) {
        reject(err);
      }
    });
  }

  
module.exports = {getUserByUUID, getUserByDiscordID, addUserToWhitelist, deleteEntryByUserID, initWhiteListDatabase};
