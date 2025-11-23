const express = require('express');
const router = express.Router();
const { body, param, validationResult } = require('express-validator');
const { runAsync, getAsync, allAsync } = require('../database');

// Middleware de validación
const handleErrors = (req, res, next) => {
  const errors = validationResult(req);
  if (!errors.isEmpty()) {
    return res.status(400).json({ success: false, errors: errors.array() });
  }
  next();
};

// ==================== USUARIOS ====================

// POST: Registrar usuario
router.post('/register', [
  body('username').trim().isLength({ min: 3 }).withMessage('Username mínimo 3 caracteres'),
  body('password').isLength({ min: 6 }).withMessage('Password mínimo 6 caracteres'),
  body('email').isEmail().withMessage('Email inválido'),
  body('profilePhotoUrl').optional().isURL().withMessage('URL inválida')
], handleErrors, async (req, res) => {
  try {
    const { username, password, email, profilePhotoUrl } = req.body;
    
    // Verificar si existe
    const existing = await getAsync(
      'SELECT id FROM users WHERE username = ? OR email = ?',
      [username, email]
    );
    
    if (existing) {
      return res.status(400).json({ success: false, error: 'Usuario o email ya existe' });
    }
    
    const now = Date.now();
    const result = await runAsync(
      `INSERT INTO users (username, password, email, profilePhotoUrl, createdAt) 
       VALUES (?, ?, ?, ?, ?)`,
      [username, password, email, profilePhotoUrl || '', now]
    );
    
    const user = await getAsync('SELECT * FROM users WHERE id = ?', [result.id]);
    res.status(201).json({ success: true, data: user });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// POST: Login
router.post('/login', [
  body('username').notEmpty(),
  body('password').notEmpty()
], handleErrors, async (req, res) => {
  try {
    const { username, password } = req.body;
    
    const user = await getAsync(
      'SELECT * FROM users WHERE username = ? AND password = ?',
      [username, password]
    );
    
    if (!user) {
      return res.status(401).json({ success: false, error: 'Credenciales inválidas' });
    }
    
    res.json({ success: true, data: user });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// GET: Obtener usuario
router.get('/:id', [
  param('id').isInt().toInt()
], handleErrors, async (req, res) => {
  try {
    const user = await getAsync('SELECT * FROM users WHERE id = ?', [req.params.id]);
    
    if (!user) {
      return res.status(404).json({ success: false, error: 'Usuario no encontrado' });
    }
    
    res.json({ success: true, data: user });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// PUT: Actualizar usuario
router.put('/:id', [
  param('id').isInt().toInt()
], handleErrors, async (req, res) => {
  try {
    const { profilePhotoUrl, highScore, level } = req.body;
    const user = await getAsync('SELECT * FROM users WHERE id = ?', [req.params.id]);
    
    if (!user) {
      return res.status(404).json({ success: false, error: 'Usuario no encontrado' });
    }
    
    const updates = [];
    const values = [];
    
    if (profilePhotoUrl !== undefined) {
      updates.push('profilePhotoUrl = ?');
      values.push(profilePhotoUrl);
    }
    
    if (highScore !== undefined) {
      updates.push('highScore = ?');
      values.push(highScore);
    }
    
    if (level !== undefined) {
      updates.push('level = ?');
      values.push(level);
    }
    
    if (updates.length > 0) {
      values.push(req.params.id);
      await runAsync(
        `UPDATE users SET ${updates.join(', ')} WHERE id = ?`,
        values
      );
    }
    
    const updated = await getAsync('SELECT * FROM users WHERE id = ?', [req.params.id]);
    res.json({ success: true, data: updated });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// ==================== JUEGOS ====================

// GET: Obtener juegos del usuario
router.get('/:userId/games', [
  param('userId').isInt().toInt()
], handleErrors, async (req, res) => {
  try {
    const games = await allAsync(
      'SELECT * FROM games WHERE userId = ? ORDER BY addedDate DESC',
      [req.params.userId]
    );
    
    res.json({ success: true, data: games });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// ==================== RUTAS DE JUEGOS ====================

router.post('/games', [
  body('userId').isInt().toInt(),
  body('name').trim().isLength({ min: 1 }),
  body('photoUrl').optional().isURL(),
  body('score').optional().isInt()
], handleErrors, async (req, res) => {
  try {
    const { userId, name, photoUrl, score } = req.body;
    
    // Verificar que usuario existe
    const user = await getAsync('SELECT id FROM users WHERE id = ?', [userId]);
    if (!user) {
      return res.status(404).json({ success: false, error: 'Usuario no encontrado' });
    }
    
    const now = Date.now();
    const result = await runAsync(
      `INSERT INTO games (userId, name, photoUrl, score, addedDate) 
       VALUES (?, ?, ?, ?, ?)`,
      [userId, name, photoUrl || '', score || 0, now]
    );
    
    const game = await getAsync('SELECT * FROM games WHERE id = ?', [result.id]);
    res.status(201).json({ success: true, data: game });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

router.put('/games/:id', [
  param('id').isInt().toInt()
], handleErrors, async (req, res) => {
  try {
    const { name, photoUrl, score } = req.body;
    
    const game = await getAsync('SELECT * FROM games WHERE id = ?', [req.params.id]);
    if (!game) {
      return res.status(404).json({ success: false, error: 'Juego no encontrado' });
    }
    
    const updates = [];
    const values = [];
    
    if (name !== undefined) {
      updates.push('name = ?');
      values.push(name);
    }
    
    if (photoUrl !== undefined) {
      updates.push('photoUrl = ?');
      values.push(photoUrl);
    }
    
    if (score !== undefined) {
      updates.push('score = ?');
      values.push(score);
    }
    
    if (updates.length > 0) {
      values.push(req.params.id);
      await runAsync(
        `UPDATE games SET ${updates.join(', ')} WHERE id = ?`,
        values
      );
    }
    
    const updated = await getAsync('SELECT * FROM games WHERE id = ?', [req.params.id]);
    res.json({ success: true, data: updated });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

router.delete('/games/:id', [
  param('id').isInt().toInt()
], handleErrors, async (req, res) => {
  try {
    const game = await getAsync('SELECT * FROM games WHERE id = ?', [req.params.id]);
    if (!game) {
      return res.status(404).json({ success: false, error: 'Juego no encontrado' });
    }
    
    await runAsync('DELETE FROM games WHERE id = ?', [req.params.id]);
    res.json({ success: true, message: 'Juego eliminado' });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

// ==================== LOGROS ====================

router.get('/games/:gameId/achievements', [
  param('gameId').isInt().toInt()
], handleErrors, async (req, res) => {
  try {
    const achievements = await allAsync(
      'SELECT * FROM achievements WHERE gameId = ? ORDER BY unlockedDate DESC',
      [req.params.gameId]
    );
    
    res.json({ success: true, data: achievements });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

router.post('/achievements', [
  body('gameId').isInt().toInt(),
  body('name').trim().isLength({ min: 1 }),
  body('description').optional().trim()
], handleErrors, async (req, res) => {
  try {
    const { gameId, name, description } = req.body;
    
    // Verificar que juego existe
    const game = await getAsync('SELECT id FROM games WHERE id = ?', [gameId]);
    if (!game) {
      return res.status(404).json({ success: false, error: 'Juego no encontrado' });
    }
    
    const now = Date.now();
    const result = await runAsync(
      `INSERT INTO achievements (gameId, name, description, unlockedDate) 
       VALUES (?, ?, ?, ?)`,
      [gameId, name, description || '', now]
    );
    
    const achievement = await getAsync('SELECT * FROM achievements WHERE id = ?', [result.id]);
    res.status(201).json({ success: true, data: achievement });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

router.delete('/achievements/:id', [
  param('id').isInt().toInt()
], handleErrors, async (req, res) => {
  try {
    const achievement = await getAsync('SELECT * FROM achievements WHERE id = ?', [req.params.id]);
    if (!achievement) {
      return res.status(404).json({ success: false, error: 'Logro no encontrado' });
    }
    
    await runAsync('DELETE FROM achievements WHERE id = ?', [req.params.id]);
    res.json({ success: true, message: 'Logro eliminado' });
  } catch (error) {
    res.status(500).json({ success: false, error: error.message });
  }
});

module.exports = router;
