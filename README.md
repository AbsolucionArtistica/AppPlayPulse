# AppPlayPulse

AppPlayPulse es una aplicación Android que permite a los usuarios ver y comparar estadísticas de juegos con sus amigos.  
Con esta app, puedes acceder a tus datos de juego, revisar cómo están rindiendo tus amigos y hacer comparaciones para mejorar juntos.

## Funcionalidades principales

- Visualización de estadísticas de juegos personales.  
- Comparación de tus datos con los de tus amigos.  
- Interfaz sencilla y amigable para seguir el rendimiento en diferentes juegos.  
- Actualización en tiempo real de las estadísticas compartidas.  

## Información adicional

Los commits hechos por **CETECOM** son realizados por los estudiantes:  
**Agustín Bahamondes**,**Diego Bahamondez**, **Vicente Candia** y **Fernanda Figueroa** desde los PC de **DuocUC**.

---

## Cambios recientes

### `FriendsMockupScreen.kt`
Nueva y completa implementación del mockup de amigos:
- Modelo `FriendData(name, phone)`.  
- UI con **TextField**, botones **“Importar contactos”**, **“Agregar amigo”** y **“Mostrar lista”**.  
- Importación de contactos con **diálogo de selección (checkboxes)** y botón “Agregar seleccionados”.  
- Detección y eliminación de duplicados por teléfono con normalización básica (mantiene dígitos y “+”).  
- `queryContacts(...)` reforzado: manejo de nulls, índices y `try/catch` para evitar crashes.  
- Uso de `LazyColumn` con altura limitada para evitar bloqueos con listas grandes.  
- Manejo de permisos en runtime y diálogo para abrir ajustes si se niega permanentemente.  
- Persistencia con `SharedPreferences` en JSON (`friends_list_json`) y **migración automática** desde formato antiguo.

### Otros archivos
- **`build.gradle.kts`** → se añadió dependencia opcional:  
  `com.googlecode.libphonenumber:libphonenumber:8.13.18`  
  *(la app incluye un fallback para compilar incluso sin esta librería).*  
- **`AndroidManifest.xml`** → añadido permiso `READ_CONTACTS`.  
- **`AnimatedSideMenu.kt` / `MainActivity.kt`** → integración de la nueva pantalla **“Amigos”**.  
- **`README.md`** → actualizado con documentación resumida de estos cambios.

---

## Motivos y mejoras principales

- **Robustez:** validaciones extra y manejo de excepciones para evitar crashes en distintos dispositivos/OEM.  
- **Escalabilidad:** `LazyColumn` optimizada para listas grandes.  
- **Persistencia segura:** migración automática sin pérdida de datos.  
- **Privacidad:** flujo de permisos claro, con opción de abrir ajustes si el usuario lo niega.  
- **Normalización:** librería `libphonenumber` disponible (opcional) para dedupe avanzado.  

---

## Cómo probar

1. Abrir el proyecto en **Android Studio**.  
2. Sincronizar dependencias (**Sync Project with Gradle Files**).  
3. Ejecutar en un **dispositivo real** o emulador con contactos creados.  
4. En la app:
   - Abrir el menú lateral → **“Amigos”**.  
   - Añadir un amigo manualmente o importar contactos.  
   - Conceder permisos cuando se soliciten.  
   - Verificar que no se añaden duplicados y que los datos persisten al reiniciar la app.  
5. Negar permisos para comprobar el diálogo de apertura de ajustes.  

---

## Recomendaciones futuras

- Activar uso directo de **libphonenumber** para normalización E.164.  
- Usar región del dispositivo para dedupe más preciso.  
- Migrar persistencia a **Room** (consultas rápidas y operaciones por lotes).  
- Procesar contactos en background para mejorar rendimiento.  
- Añadir **tests unitarios e instrumentados** (normalización, migración y permisos).  
