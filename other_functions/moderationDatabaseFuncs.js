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

function newPunishment(adminID, punishedUserID, punishmentType, punishmentReason = null, punishmentDurationStamp = null) {
  return new Promise((resolve, reject) => {

    const allowedPunishmentTypes = ['KICK', 'BAN', 'MUTE', 'TEMPBAN', 'WARN'];

    if (!allowedPunishmentTypes.includes(punishmentType)) {
      return reject(new Error("Invalid punishment type. Allowed values are 'KICK', 'BAN', 'MUTE', 'TEMPBAN', 'WARN'."));
    }

    const query = `INSERT INTO punishments (adminID, punishedUserID, punishmentType, punishmentReason, punishmentDate, punishmentExpirationTime) VALUES (?, ?, ?, ?, ?, ?)`;

    try {
      if ((punishmentType === 'MUTE' || punishmentType === 'TEMPBAN') && punishmentDurationStamp === null) {
        return reject(new Error("Punishment duration is required for MUTE and TEMPBAN punishments."));
      }

      const stmt = db.prepare(query);
      const info = stmt.run(adminID, punishedUserID, punishmentType, punishmentReason, Math.floor(Date.now() / 1000), punishmentDurationStamp);


      resolve(info.lastInsertRowid);
    } catch (err) {
      reject(err);
    }
  });
}

function deletePunishmentByID(punishmentID) {
  return new Promise((resolve, reject) => {
    const query = `DELETE FROM punishments WHERE id = ?`;
    try {
      const stmt = db.prepare(query);
      const info = stmt.run(punishmentID);
      resolve(info.changes);
    } catch (err) {
      reject(err);
    }
  });
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


module.exports = { initModerationDatabase, newPunishment, getExpiredPunishments, getUsersPunishments, getAdminsPunishments, deletePunishmentByID};
