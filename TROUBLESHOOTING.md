# Troubleshooting - Problemas Comunes

## ❌ Servidor no inicia

**Error:** `EADDRINUSE: address already in use :::3000`

```bash
# Matar proceso en puerto 3000
# Windows
netstat -ano | findstr :3000
taskkill /PID [PID] /F

# Mac/Linux
lsof -i :3000
kill -9 [PID]
```

---

## ❌ App no conecta al servidor

**Error:** "Servidor no disponible"

**Soluciones:**

1. Verifica servidor está corriendo:
```bash
curl http://localhost:3000/health
```

2. Para emulador Android, la URL debe ser:
```kotlin
http://10.0.2.2:3000/
```

3. Para dispositivo físico:
```kotlin
http://TU_IP_LOCAL:3000/
```

4. Verifica firewall permite puerto 3000

---

## ❌ Datos no sincronizan

**Causa:** App está offline

**Solución:** Los datos se guardan localmente y se sincronizan automáticamente cuando hay conexión.

---

## ❌ Error: "Database locked"

```bash
# Reinicia servidor
# Ctrl+C en terminal y luego npm run dev
```

---

## ❌ Gradle no compila

```bash
# Limpiar proyecto
./gradlew clean
./gradlew build
```

---

## ❌ Permisos de internet

Verifica `AndroidManifest.xml` tenga:
```xml
<uses-permission android:name="android.permission.INTERNET" />
```

---

## 🔍 Verificar todo funciona

### Terminal 1: Servidor
```bash
npm run dev
```

### Terminal 2: Prueba API
```bash
curl http://localhost:3000/health
```

### Terminal 3: Logs Android
```bash
adb logcat | grep MainViewModel
```

Si todo muestra mensajes sin errores, ¡está funcionando! ✅
