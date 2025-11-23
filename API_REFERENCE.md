# AppPlayPulse - API Reference

## Estructura de Datos

### User
```json
{
  "id": 1,
  "username": "player1",
  "password": "secure_password",
  "email": "player@example.com",
  "profilePhotoUrl": "http://...",
  "highScore": 5000,
  "level": 10,
  "createdAt": 1700000000000
}
```

### Game
```json
{
  "id": 1,
  "userId": 1,
  "name": "Super Mario",
  "photoUrl": "http://...",
  "score": 2500,
  "addedDate": 1700000000000
}
```

### Achievement
```json
{
  "id": 1,
  "gameId": 1,
  "name": "First Victory",
  "description": "Gana el primer nivel",
  "unlockedDate": 1700000000000
}
```

---

## Endpoints

### Authentication

**POST /api/users/register**
```bash
curl -X POST http://localhost:3000/api/users/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "player1",
    "password": "pass123",
    "email": "player@example.com",
    "profilePhotoUrl": "http://photo.jpg"
  }'
```

**POST /api/users/login**
```bash
curl -X POST http://localhost:3000/api/users/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "player1",
    "password": "pass123"
  }'
```

### User

**GET /api/users/:id**
```bash
curl http://localhost:3000/api/users/1
```

**PUT /api/users/:id**
```bash
curl -X PUT http://localhost:3000/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "profilePhotoUrl": "http://new-photo.jpg",
    "highScore": 6000
  }'
```

### Games

**GET /api/users/:userId/games**
```bash
curl http://localhost:3000/api/users/1/games
```

**POST /api/games**
```bash
curl -X POST http://localhost:3000/api/games \
  -H "Content-Type: application/json" \
  -d '{
    "userId": 1,
    "name": "Super Mario",
    "photoUrl": "http://...",
    "score": 2500
  }'
```

**PUT /api/games/:id**
```bash
curl -X PUT http://localhost:3000/api/games/1 \
  -H "Content-Type: application/json" \
  -d '{
    "score": 3000
  }'
```

**DELETE /api/games/:id**
```bash
curl -X DELETE http://localhost:3000/api/games/1
```

### Achievements

**GET /api/games/:gameId/achievements**
```bash
curl http://localhost:3000/api/games/1/achievements
```

**POST /api/achievements**
```bash
curl -X POST http://localhost:3000/api/achievements \
  -H "Content-Type: application/json" \
  -d '{
    "gameId": 1,
    "name": "First Victory",
    "description": "Gana el primer nivel"
  }'
```

**DELETE /api/achievements/:id**
```bash
curl -X DELETE http://localhost:3000/api/achievements/1
```

---

## Estados de Respuesta

- `200 OK` - Éxito
- `201 Created` - Recurso creado
- `400 Bad Request` - Datos inválidos
- `404 Not Found` - Recurso no encontrado
- `500 Server Error` - Error del servidor

---

## Comandos Útiles

**Health check:**
```bash
curl http://localhost:3000/health
```

**Ver todos los usuarios:**
```bash
curl http://localhost:3000/api/users
```

**Crear usuario y juego completo:**
```bash
# 1. Crear usuario
USER_ID=$(curl -s -X POST http://localhost:3000/api/users/register \
  -H "Content-Type: application/json" \
  -d '{"username":"test","password":"123","email":"test@test.com"}' | grep -o '"id":[0-9]*' | cut -d: -f2)

# 2. Crear juego
GAME_ID=$(curl -s -X POST http://localhost:3000/api/games \
  -H "Content-Type: application/json" \
  -d "{\"userId\":$USER_ID,\"name\":\"Test Game\",\"score\":100}" | grep -o '"id":[0-9]*' | cut -d: -f2)

# 3. Crear logro
curl -s -X POST http://localhost:3000/api/achievements \
  -H "Content-Type: application/json" \
  -d "{\"gameId\":$GAME_ID,\"name\":\"First Achievement\"}"
```
