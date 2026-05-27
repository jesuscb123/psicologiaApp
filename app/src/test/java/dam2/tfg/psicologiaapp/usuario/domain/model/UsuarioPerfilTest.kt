package dam2.tfg.psicologiaapp.usuario.domain.model

import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioPerfilTest {

    @Test
    fun `nombreCompleto concatena nombre y apellidos correctamente`() {
        val perfil = UsuarioPerfilBasico(
            usuarioId = 1,
            firebaseUid = "uid",
            nombre = "Juan",
            apellidos = "Pérez García",
            email = "juan@test.com",
            fotoPerfilUrl = null
        )

        assertEquals("Juan Pérez García", perfil.nombreCompleto())
    }

    @Test
    fun `nombreCompleto maneja apellidos vacios`() {
        val perfil = UsuarioPerfilBasico(
            usuarioId = 1,
            firebaseUid = "uid",
            nombre = "Juan",
            apellidos = "",
            email = "juan@test.com",
            fotoPerfilUrl = null
        )

        assertEquals("Juan", perfil.nombreCompleto())
    }

    @Test
    fun `nombreCompleto maneja nombre vacio`() {
        val perfil = UsuarioPerfilBasico(
            usuarioId = 1,
            firebaseUid = "uid",
            nombre = "",
            apellidos = "Pérez",
            email = "juan@test.com",
            fotoPerfilUrl = null
        )

        assertEquals("Pérez", perfil.nombreCompleto())
    }

    @Test
    fun `nombreCompleto funciona en PacientePerfil`() {
        val perfil = PacientePerfil(
            usuarioId = 1,
            firebaseUid = "uid",
            nombre = "María",
            apellidos = "García",
            email = "maria@test.com",
            fotoPerfilUrl = null,
            psicologoId = 10L,
        )

        assertEquals("María García", perfil.nombreCompleto())
    }

    @Test
    fun `nombreCompleto funciona en PsicologoPerfil`() {
        val perfil = PsicologoPerfil(
            usuarioId = 2,
            firebaseUid = "uid-psi",
            nombre = "Luis",
            apellidos = "Martín",
            email = "luis@test.com",
            fotoPerfilUrl = null,
            numeroColegiado = "99999",
            especialidades = listOf("Clínica"),
        )

        assertEquals("Luis Martín", perfil.nombreCompleto())
    }

    @Test
    fun `nombreCompleto devuelve cadena vacia si ambos campos estan vacios`() {
        val perfil = UsuarioPerfilBasico(
            usuarioId = 1,
            firebaseUid = "uid",
            nombre = "",
            apellidos = "",
            email = "test@test.com",
            fotoPerfilUrl = null,
        )

        assertEquals("", perfil.nombreCompleto())
    }
}
