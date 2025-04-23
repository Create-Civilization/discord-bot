const Database = require('better-sqlite3');
const fs = require('fs');
const path = require('path');

const dbPath = './whitelistData.db';
let db;
const collumns = {
  id: "INTEGER PRIMARY KEY AUTOINCREMENT",
  playerUUID: "TEXT NOT NULL UNIQUE -- The UUID of the Minecraft player, must be unique",
  discordID: "TEXT NOT NULL UNIQUE -- The Discord ID of the player, must be unique",
  username: "TEXT NOT NULL",
  reason: "TEXT NOT NULL",
  createdAt: "INTEGER DEFAULT (strftime('%s', 'now')) -- Time when the record was created",
  bans: "INTEGER DEFAULT 0",
  bannedAt: "INTEGER DEFAULT 0",
  bannedUntil: "INTEGER DEFAULT 0"
}

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
          createdAt INTEGER DEFAULT (strftime('%s', 'now')),  -- Time when the record was created
          bans INTEGER DEFAULT 0,
          bannedAt INTEGER DEFAULT 0,
          bannedUntil INTEGER DEFAULT 0
        )
    `).run();
    

    console.log('Database created and table "whitelistData" initialized.');
  } else {
    console.log('Database exists. Opening the database...');
    db = new Database(dbPath); 
    try {
      const stmt = db.pragma('table_info(whitelistData)');
      const collumnNames = stmt.map(col => col.name);

      for (const key of Object.keys(collumns)) {
        if (!collumnNames.includes(key)) {
          console.log(`Collumn \"${key}\" not found, adding collumn`)
          db.prepare(`ALTER TABLE whitelistData ADD COLUMN ${key} ${collumns[key]}`).run()
        }
      }
    } catch (err) {
      throw err;
    }
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
function getUserByDiscordID(discordID = null) {
  return new Promise((resolve, reject) => {
    let query;
    let params = [];
    let row;

    if(discordID){
      query = `SELECT * FROM whitelistData WHERE discordID = ?`;
      params = [discordID];
    } else {
      query = `SELECT * FROM whitelistData`;
    }

    try {
      if(discordID){
        row = db.prepare(query).get(...params);
      } else if(!discordID) {
        row = db.prepare(query).all(...params); 
      }
      resolve(row);
    } catch (err) {
      reject(err);
    }
  });
}


function getUserByMinecraftUsername(username = null) {
  return new Promise((resolve, reject) => {
    let query;
    let params = [];

    if(username){
      query = `SELECT * FROM whitelistData WHERE username = ?`;
      params = [username];
    } else {
      query = `SELECT * FROM whitelistData`;
    }

    try {
      if(username){
        row = db.prepare(query).get(...params);
      } else if(!username) {
        row = db.prepare(query).all(...params); 
      }
      resolve(row);
    } catch (err) {
      reject(err);
    }

  });
}

function setBan(discordID, bannedAt, bannedUntil) {
  db.prepare(`UPDATE whitelistData SET bannedAt = ${bannedAt}, bannedUntil = ${bannedUntil} WHERE discordID = ${discordID}`).run();
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

function getAllWhitelistData() {
  return new Promise((resolve, reject) => {
    const query = `SELECT * FROM whitelistData`;
    try {
      const stmt = db.prepare(query);
      const rows = stmt.all();
      resolve(rows);
    } catch (err) {
      reject(err);
    }
  });
}

function getExpiredBans() {
  return new Promise((resolve, reject) => {
    const query = `SELECT * FROM whitelistData WHERE bannedAt > 0 AND bannedUntil <= (strftime('%s', 'now'))`
    try {
      const stmt = db.prepare(query);
      const rows = stmt.all(); 
      resolve(rows);
    } catch (err) {
      reject(err);
    }
  })
}

function setUserUsername(searchBy, searchValue, newUsername) {
  return new Promise((resolve, reject) => {
    const query = `UPDATE whitelistData SET username = ? WHERE ${searchBy} = ?`;
    try {
      const stmt = db.prepare(query);
      const info = stmt.run(newUsername, searchValue);
      resolve(info.changes);
    } catch (err) {
      reject(err);
    }
  });
}


module.exports = {
  getUserByMinecraftUsername, 
  getUserByUUID, 
  getUserByDiscordID, 
  addUserToWhitelist, 
  deleteEntryByUserID, 
  initWhiteListDatabase, 
  getAllWhitelistData, 
  setUserUsername,
  getExpiredBans,
  setBan
};
