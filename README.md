# AppPlayPulse

AppPlayPulse es una aplicación Android que permite a los usuarios ver y comparar estadísticas de juegos con sus amigos.  
Con esta app, puedes acceder a tus datos de juego, revisar cómo están rindiendo tus amigos y hacer comparaciones para mejorar juntos.

##  Funcionalidades principales

- Visualización de estadísticas de juegos personales.  
- Comparación de tus datos con los de tus amigos.  
- Interfaz sencilla y amigable para seguir el rendimiento en diferentes juegos.  
- Actualización en tiempo real de las estadísticas compartidas.  

##  Información adicional

Los commits hechos por **CETECOM** son realizados por los estudiantes:  
**Agustín Bahamondes**, **Vicente Candia** y **Fernanda Figueroa**, desde los PC de **DuocUC**.

---

##  Cambios recientes 

Breve resumen de los cambios aplicados durante esta sesión:

- Se agregó una entrada **"Amigos"** en el menú lateral (`AnimatedSideMenu`) que abre un mockup de sistema de amigos.  
- Se creó **`FriendsMockupScreen`** en `app/src/main/java/com/example/appplaypulse_grupo4/ui/screens/FriendsMockupScreen.kt`:  
  - Permite ingresar nombres, guardarlos localmente en `SharedPreferences` como JSON y mostrarlos en una `LazyColumn`.  
  - Incluye un `@Preview` para revisión rápida en Android Studio.  
- Se mejoró el manejo de `Context` en Composables usando `LocalContext.current` para evitar referencias directas a la `Activity`.  
- Archivos modificados:  
  - `AnimatedSideMenu.kt`  
  - `MainActivity.kt`  
  - `ui/screens/FriendsMockupScreen.kt`  

>  Nota: Estos cambios corresponden a un mockup funcional y **no implementan aún un sistema completo de amigos ni integración de red.**

---

##  Notas técnicas adicionales

- **Permisos:**  
  Prueba esta funcionalidad en un dispositivo real. Algunos emuladores no poseen contactos; si usas uno, crea contactos manualmente en el AVD.  

- **Seguridad / Privacidad:**  
  Los contactos se guardan únicamente de forma local. Si en el futuro se agregan funciones de carga o sincronización, debe solicitarse consentimiento explícito al usuario.  

- **Selección de contactos:**  
  Actualmente la interfaz añade todos los contactos con el botón **“Agregar todos”**. Puede implementarse selección individual (checkboxes) en futuras versiones.  

- **Duplicados:**  
  El sistema evita duplicados por nombre usando una verificación `contains`, aunque se recomienda mejorar la lógica comparando números de teléfono o aplicando normalización.  

- **Compatibilidad:**  
  Usa APIs estándar de `ContactsContract` y funciona correctamente en **API 24+** (coincide con `minSdk 24` del proyecto).  

---
