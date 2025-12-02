package com.example.appplaypulse_grupo4.validation

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class FormValidatorsTest {

    @Test
    fun `email must have valid domain`() {
        val error = FormValidators.validateEmail("player@bad")
        assertEquals("Dominio de correo invalido", error)
    }

    @Test
    fun `password enforces strength rules`() {
        assertNull(FormValidators.validatePassword("Aa1!aaaa"))
        val weakError = FormValidators.validatePassword("weakpass")
        assertEquals(
            "Contrasena: 8+ caracteres, mayuscula, minuscula, numero y simbolo",
            weakError
        )
    }
}
