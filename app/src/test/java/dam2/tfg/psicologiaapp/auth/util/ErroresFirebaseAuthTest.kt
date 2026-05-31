package dam2.tfg.psicologiaapp.auth.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ErroresFirebaseAuthTest {

    @Test
    fun mensajeErrorCreacionCuenta_traduceCorreoDuplicadoPorMensaje() {
        val error = Exception("The email address is already in use by another account.")
        assertEquals("Este correo electrónico ya está registrado", error.mensajeErrorCreacionCuenta())
    }

    @Test
    fun mensajeErrorCreacionCuenta_traduceCorreoDuplicadoPorCodigoFirebase() {
        val error = Exception("ERROR_EMAIL_ALREADY_IN_USE")
        assertEquals("Este correo electrónico ya está registrado", error.mensajeErrorCreacionCuenta())
    }
}
