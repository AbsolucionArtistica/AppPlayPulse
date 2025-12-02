package com.example.appplaypulse_grupo4.validation

/**
 * Validaciones reutilizables para formularios.
 * Devuelven null cuando el valor es valido; de lo contrario, un mensaje de error.
 */
object FormValidators {
    private val nameRegex = Regex("^[A-Za-z ]{2,40}$")
    private val usernameRegex = Regex("^[A-Za-z0-9_]{4,20}$")
    private val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    private val phoneRegex = Regex("^\\+56\\s?9\\d{8}$")
    private val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#\$%^&*()_+=-]).{8,}$")

    fun validateRequired(value: String, label: String): String? =
        if (value.isBlank()) "$label es obligatorio" else null

    fun validateName(value: String, label: String = "Nombre"): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return "$label es obligatorio"
        if (!nameRegex.matches(trimmed)) return "$label solo acepta letras y espacios (2-40)"
        return null
    }

    fun validateAge(value: String, min: Int = 12, max: Int = 100): String? {
        val number = value.toIntOrNull() ?: return "Edad debe ser numerica"
        if (number !in min..max) return "Edad debe estar entre $min y $max"
        return null
    }

    fun validateEmail(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return "Correo es obligatorio"
        if (!emailRegex.matches(trimmed)) return "Correo invalido"

        val domain = trimmed.substringAfter("@")
        if (!domain.contains(".")) return "Dominio de correo invalido"
        if (domain.length < 4) return "Dominio de correo demasiado corto"
        return null
    }

    fun validatePhone(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return "Telefono es obligatorio"
        if (!phoneRegex.matches(trimmed)) return "Telefono invalido. Usa +56 9 XXXXXXXX"
        return null
    }

    fun validateUsername(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return "Usuario es obligatorio"
        if (!usernameRegex.matches(trimmed)) return "Usuario solo letras/numeros/_ (4-20)"
        return null
    }

    fun validatePassword(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) return "Contrasena es obligatoria"
        if (!passwordRegex.matches(trimmed)) {
            return "Contrasena: 8+ caracteres, mayuscula, minuscula, numero y simbolo"
        }
        return null
    }
}
