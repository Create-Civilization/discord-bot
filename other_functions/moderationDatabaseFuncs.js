const Database = require('better-sqlite3');
const fs = require('fs');
const path = require('path');

let db;
const dbPath = './moderationDatabase.db';

function initModerationDatabase() {
  const dbExists = fs.existsSync(dbPath);

  if (!dbExists) {
    console.log("Database doesn't exist. Creating new database...");


    db = new Database(dbPath);

    db.prepare(`
      CREATE TABLE IF NOT EXISTS punishments (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        adminID TEXT NOT NULL,
        punishedUserID TEXT NOT NULL,
        punishedUserMinecraft TEXT NOT NULL,
        punishmentType TEXT CHECK(punishmentType IN ('KICK', 'BAN', 'MUTE', 'TEMPBAN', 'WARN')) NOT NULL,
        punishmentReason TEXT,
        punishmentDate INTEGER DEFAULT (strftime('%s', 'now')),
        punishmentExpirationTime INTEGER
      )
    `).run();

    console.log('Database created and table "punishments" initialized.');
  } else {
    console.log('Database exists. Opening the database...');
    db = new Database(dbPath); 
  }

  return db;
}

function newPunishment(adminID, punishedUserID, punishedUserMinecraft, punishmentType, punishmentReason = null, punishmentDurationStamp = null) {
  return new Promise((resolve, reject) => {

    const allowedPunishmentTypes = ['KICK', 'BAN', 'MUTE', 'TEMPBAN', 'WARN'];

    if (!allowedPunishmentTypes.includes(punishmentType)) {
      return reject(new Error("Invalid punishment type. Allowed values are 'KICK', 'BAN', 'MUTE', 'TEMPBAN', 'WARN'."));
    }

    const query = `INSERT INTO punishments (adminID, punishedUserID, punishedUserMinecraft, punishmentType, punishmentReason, punishmentDate, punishmentExpirationTime) VALUES (?, ?, ?, ?, ?, ?, ?)`;

    try {
      if ((punishmentType === 'MUTE' || punishmentType === 'TEMPBAN') && punishmentDurationStamp === null) {
        return reject(new Error("Punishment duration is required for MUTE and TEMPBAN punishments."));
      }

      const stmt = db.prepare(query);
      const info = stmt.run(adminID, punishedUserID, punishedUserMinecraft, punishmentType, punishmentReason, Math.floor(Date.now() / 1000), punishmentDurationStamp);


      resolve(info.lastInsertRowid);
    } catch (err) {
      reject(err);
    }
  });
}

function deletePunishmentByID(punishmentID) {
  db.prepare(`DELETE FROM punishments WHERE id == ${punishmentID}`).run();
}


function getExpiredPunishments() {
  return new Promise((resolve, reject) => {
    const query = `SELECT * FROM punishments WHERE punishmentExpirationTime <= ?`;
    try {
      const stmt = db.prepare(query);
      const currentTimestamp = Math.floor(Date.now() / 1000);  
      const rows = stmt.all(currentTimestamp);
      resolve(rows);
    } catch (err) {
      reject(err); 
    }
  });
}

function getExpiredBans() {
  return new Promise((resolve, reject) => {
    const query = `SELECT * FROM punishments WHERE punishmentExpirationTime <= ? AND punishmentType == \'TEMPBAN\'`;
    try {
      const stmt = db.prepare(query);
      const currentTimestamp = Math.floor(Date.now() / 1000);  
      const rows = stmt.all(currentTimestamp);
      resolve(rows);
    } catch (err) {
      reject(err); 
    }
  });
}


function getUsersPunishments(userID) {
  return new Promise((resolve, reject) => {
    const query = `SELECT * FROM punishments WHERE punishedUserID = ?`;
    try {
      const stmt = db.prepare(query);
      const rows = stmt.all(userID);
      resolve(rows);
    } catch (err) {
      reject(err);
    }
  });
}

async function isBanned(userID) {
  const punishments = await getUsersPunishments(userID);
  if (punishments == undefined) return false;
  for (let punishment of punishments) {
    if (punishment.punishmentType === 'BAN') {
      return true;
    }
  }
  return false;
}


function getAdminsPunishments(adminID) {
  return new Promise((resolve, reject) => {
    const query = `SELECT * FROM punishments WHERE adminID = ?`;
    try {
      const stmt = db.prepare(query);
      const rows = stmt.all(adminID);
      resolve(rows);
    } catch (err) {
      reject(err);
    }
  });
}


module.exports = { initModerationDatabase, newPunishment, getExpiredPunishments, getUsersPunishments, getAdminsPunishments, deletePunishmentByID, isBanned, getExpiredBans};
