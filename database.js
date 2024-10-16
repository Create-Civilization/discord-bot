import Database from 'better-sqlite3';
import fs from 'fs';
import path from 'path';

let db;
const dbPath = './tickets.db';

export function initDatabase() {
  const dbExists = fs.existsSync(dbPath);

  if (!dbExists) {
    console.log("Database doesn't exist. Creating new database...");


    db = new Database(dbPath);

    db.prepare(`
      CREATE TABLE IF NOT EXISTS tickets (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        authorID TEXT NOT NULL,
        threadChannelID TEXT NOT NULL,
        embedMessageID TEXT NOT NULL,
        created_at TEXT DEFAULT CURRENT_TIMESTAMP
      )
    `).run();

    console.log('Database created and table "tickets" initialized.');
  } else {
    console.log('Database exists. Opening the database...');
    db = new Database(dbPath); 
  }

  return db;
}

function insertTicket(authorId, threadId, embedMessageID) {
  return new Promise((resolve, reject) => {
    const query = `INSERT INTO tickets (authorID, threadChannelID, embedMessageID) VALUES (?, ?, ?)`;
    try {
      const stmt = db.prepare(query);
      const info = stmt.run(authorId, threadId, embedMessageID);
      resolve(info.lastInsertRowid); 
    } catch (err) {
      reject(err);
    }
  });
}

function getTicketByAuthor(authorId) {
  return new Promise((resolve, reject) => {
    const query = `SELECT * FROM tickets WHERE authorID = ?`;
    try {
      const stmt = db.prepare(query);
      const row = stmt.get(authorId);
      resolve(row); 
    } catch (err) {
      reject(err);
    }
  });
}

function getTicketByChannel(channelId) {
    return new Promise((resolve, reject) => {
      const query = `SELECT * FROM tickets WHERE threadChannelID = ?`;
      try {
        const stmt = db.prepare(query);
        const row = stmt.get(channelId);
        resolve(row); 
      } catch (err) {
        reject(err);
      }
    });
  }


function deleteTicketByTicketID(ticketId) {
    return new Promise((resolve, reject) => {
      const query = `DELETE FROM tickets WHERE id = ?`;
      try {
        const stmt = db.prepare(query);
        const info = stmt.run(ticketId);
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

export { getTicketByAuthor, insertTicket, deleteTicketByTicketID , getTicketByChannel};
