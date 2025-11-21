const sqlite3 = require('sqlite3').verbose();
const path = require('path');

const dbPath = process.env.DATABASE_PATH || path.join(__dirname, 'appplaypulse.db');

const db = new sqlite3.Database(dbPath, (err) => {
  if (err) {
    console.error('Error opening database:', err.message);
  } else {
    console.log('Connected to SQLite database');
    initializeDatabase();
  }
});

function runAsync(sql, params = []) {
  return new Promise((resolve, reject) => {
    db.run(sql, params, function(err) {
      if (err) {
        reject(err);
      } else {
        resolve({ id: this.lastID, changes: this.changes });
      }
    });
  });
}

function getAsync(sql, params = []) {
  return new Promise((resolve, reject) => {
    db.get(sql, params, (err, row) => {
      if (err) {
        reject(err);
      } else {
        resolve(row);
      }
    });
  });
}

function allAsync(sql, params = []) {
  return new Promise((resolve, reject) => {
    db.all(sql, params, (err, rows) => {
      if (err) {
        reject(err);
      } else {
        resolve(rows || []);
      }
    });
  });
}

function initializeDatabase() {
  // Usuarios
  db.run(`
    CREATE TABLE IF NOT EXISTS users (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      username TEXT UNIQUE NOT NULL,
      password TEXT NOT NULL,
      email TEXT UNIQUE NOT NULL,
      profilePhotoUrl TEXT,
      highScore INTEGER DEFAULT 0,
      level INTEGER DEFAULT 1,
      createdAt INTEGER NOT NULL
    )
  `);

  // Juegos
  db.run(`
    CREATE TABLE IF NOT EXISTS games (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      userId INTEGER NOT NULL,
      name TEXT NOT NULL,
      photoUrl TEXT,
      score INTEGER DEFAULT 0,
      addedDate INTEGER NOT NULL,
      FOREIGN KEY(userId) REFERENCES users(id) ON DELETE CASCADE
    )
  `);

  // Logros
  db.run(`
    CREATE TABLE IF NOT EXISTS achievements (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      gameId INTEGER NOT NULL,
      name TEXT NOT NULL,
      description TEXT,
      unlockedDate INTEGER NOT NULL,
      FOREIGN KEY(gameId) REFERENCES games(id) ON DELETE CASCADE
    )
  `);

  // Índices
  db.run('CREATE INDEX IF NOT EXISTS idx_games_userId ON games(userId)');
  db.run('CREATE INDEX IF NOT EXISTS idx_achievements_gameId ON achievements(gameId)');
}

module.exports = {
  db,
  runAsync,
  getAsync,
  allAsync
};
