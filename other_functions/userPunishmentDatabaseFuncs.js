import Database from 'better-sqlite3';
import fs from 'fs';
import path, { resolve } from 'path';

let db;
const dbPath = './userPunishments.db';

export function initDatabase() {
  const dbExists = fs.existsSync(dbPath);

  if (!dbExists) {
    console.log("Database doesn't exist. Creating new database...");


    db = new Database(dbPath);

    db.prepare(`
        CREATE TABLE IF NOT EXISTS userPunishments (
          id INTEGER PRIMARY KEY AUTOINCREMENT,
          userID TEXT NOT NULL,  -- The ID of the user being punished
          actionType TEXT NOT NULL,  -- e.g., "warn", "temp_mute", "temp_ban", "perma_ban"
          reason TEXT NOT NULL,  -- Reason for the punishment
          expiresAt INTEGER,  -- Expiration time for temp mutes or bans, NULL for warnings and permanent bans
          issuedAt INTEGER DEFAULT (strftime('%s', 'now')),  -- Time when the action was issued
          issuedBy TEXT NOT NULL  -- The ID of the staff member who issued the punishment
        )
      `).run();      

    console.log('Database created and table "userPunishments" initialized.');
  } else {
    console.log('Database exists. Opening the database...');
    db = new Database(dbPath); 
  }

  return db;
}


function addTempBanToDatabase(punishedUserID, actionType, reason, expiresAtInUNIX, issuedByID){
    return new Promise((resolve, reject) => {
        const query = `INSERT INTO userPunishments (userID, actionType, reason, expiresAt, issuedBy) VALUES (?,?,?,?,?)`;
        try {
            const stmt = db.prepare(query);
            const info = stmt.run(punishedUserID,actionType,reason,expiresAtInUNIX,issuedByID);
            resolve(info.lastInsertRowid);
        } catch(err) {
            reject(err)
        }
    });

}

function getPunishmentsByUserID(punishedUserID, actionType) {
    return new Promise((resolve, reject) => {
      const query = `SELECT * FROM userPunishments WHERE userID = ? AND actionType = ?`;
      db.all(query, [punishedUserID, actionType], (err, rows) => {
        if (err) {
          reject(err);
        } else {
          resolve(rows);
        }
      });
    });
}  

export {addTempBanToDatabase, getPunishmentsByUserID};
