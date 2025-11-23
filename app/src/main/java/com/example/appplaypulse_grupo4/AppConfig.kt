package com.example.appplaypulse_grupo4

/**
 * Configuración centralizada de la aplicación
 */
object AppConfig {

    // ============================================
    // API CONFIGURATION
    // ============================================

    /**
     * URL base del servidor backend
     *
     * - Para emulador Android Studio: http://10.0.2.2:3000/
     * - Para dispositivo físico: http://<tu-pc-ip>:3000/
     * - Para localhost: http://localhost:3000/
     *
     * Nota: El servidor debe estar ejecutándose en el puerto 3000
     */
    const val API_BASE_URL = "http://10.0.2.2:3000/"

    /**
     * Timeout para conexiones HTTP (en segundos)
     */
    const val CONNECTION_TIMEOUT = 30

    /**
     * Timeout para lectura de respuestas HTTP (en segundos)
     */
    const val READ_TIMEOUT = 30

    /**
     * Timeout para escritura de solicitudes HTTP (en segundos)
     */
    const val WRITE_TIMEOUT = 30

    // ============================================
    // DATABASE CONFIGURATION
    // ============================================

    /**
     * Nombre de la base de datos local (Room)
     */
    const val DATABASE_NAME = "appplaypulse.db"

    /**
     * Versión inicial de la base de datos
     */
    const val DATABASE_VERSION = 1

    // ============================================
    // USER PREFERENCES
    // ============================================

    /**
     * Nombre del archivo de preferencias compartidas para usuario actual
     */
    const val USER_PREFERENCES = "user_prefs"

    /**
     * Claves para SharedPreferences
     */
    object PreferenceKeys {
        const val USER_ID = "user_id"
        const val USERNAME = "username"
        const val EMAIL = "email"
        const val IS_LOGGED_IN = "is_logged_in"
    }
}
