# AppPlayPulse

AppPlayPulse es una aplicación Android que permite a los usuarios ver y comparar estadísticas de juegos con sus amigos. Con esta app, puedes acceder a tus datos de juego, revisar cómo están rindiendo tus amigos y hacer comparaciones para mejorar juntos.

## Funcionalidades principales

- Visualización de estadísticas de juegos personales.
- Comparación de tus datos con los de tus amigos.
- Interfaz sencilla y amigable para seguir el rendimiento en diferentes juegos.
- Actualización en tiempo real de las estadísticas compartidas.

## Información Adicional
- Los commits hechos por CETECOM son realizados por los estudiantes Agustín Bahamondes, Vicente Candia y Fernanda Figueroa desde los PC de DuocUC.

## Cambios recientes 

Breve resumen de cambios aplicados por el asistente durante esta sesión:

 - Se agregó una entrada "Amigos" en el menú lateral (`AnimatedSideMenu`) que abre un mockup de sistema de amigos.
 - Se creó `FriendsMockupScreen` en `app/src/main/java/com/example/appplaypulse_grupo4/ui/screens/FriendsMockupScreen.kt`:
	- Permite ingresar nombres, guardarlos localmente en `SharedPreferences` como JSON y mostrarlos en una `LazyColumn`.
	- Incluye un `@Preview` para revisión rápida en Android Studio.
- Se mejoró el manejo de `Context` en Composables usando `LocalContext.current` para evitar referencias directas a la Activity.
- Archivos modificados: `AnimatedSideMenu.kt`, `MainActivity.kt`, `ui/screens/FriendsMockupScreen.kt`.

Nota: Estos cambios son un mockup funcional y no implementan un sistema de amigos completo ni integración de red.
