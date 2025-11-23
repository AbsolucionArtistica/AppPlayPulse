# AppPlayPulse - Instalación Rápida

## 🚀 Inicio en 5 minutos

### Paso 1: Instalar dependencias del servidor
```bash
cd server
npm install
```

### Paso 2: Iniciar servidor
**Windows:**
```bash
cd scripts
start-server.bat
```

**Mac/Linux:**
```bash
bash scripts/start-server.sh
```

Deberías ver:
```
🚀 Servidor corriendo en http://localhost:3000
```

### Paso 3: Ejecutar app en Android Studio
1. Abrir proyecto en Android Studio
2. Clic en botón ▶ (Run)
3. Seleccionar emulador o dispositivo
4. Esperar compilación

### Paso 4: Probar en la app
- Crear usuario (username, password, email, foto)
- Agregar un juego
- Agregar logros al juego
- Ver perfil

---

## ⚙️ Configuración

### Para dispositivo físico
Editar `AppConfig.kt`:
```kotlin
const val API_BASE_URL = "http://TU_IP:3000/"
```

Obtener IP:
```bash
ipconfig    # Windows
ifconfig    # Mac/Linux
```

---

## 📊 Estructura Base de Datos

### Tabla: users
- id (PrimaryKey)
- username (único)
- password
- email (único)
- profilePhotoUrl
- highScore
- level
- createdAt

### Tabla: games
- id (PrimaryKey)
- userId (ForeignKey → users)
- name
- photoUrl
- score
- addedDate

### Tabla: achievements
- id (PrimaryKey)
- gameId (ForeignKey → games)
- name
- description
- unlockedDate

---

## 🔗 API Endpoints

```
POST   /api/users/register             Crear usuario
POST   /api/users/login                Login
GET    /api/users/:id                  Obtener usuario
PUT    /api/users/:id                  Actualizar usuario

GET    /api/users/:userId/games        Obtener juegos del usuario
POST   /api/games                      Crear juego
PUT    /api/games/:id                  Actualizar juego
DELETE /api/games/:id                  Eliminar juego

GET    /api/games/:gameId/achievements Obtener logros del juego
POST   /api/achievements               Crear logro
DELETE /api/achievements/:id           Eliminar logro
```

---

## ✅ Lo que incluye

✅ BD local (Room/SQLite) con Users, Games, Achievements
✅ Servidor REST con autenticación básica
✅ Sincronización automática online/offline
✅ Manejo de errores
✅ Validación de entrada

---

## 📝 Notas

- Cambiar contraseñas después de pruebas iniciales en producción
- Usar HTTPS en producción
- No guardar contraseñas en texto plano (implementar hashing)
