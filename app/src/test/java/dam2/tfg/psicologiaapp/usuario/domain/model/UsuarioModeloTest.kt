package dam2.tfg.psicologiaapp.usuario.domain.model

import dam2.tfg.psicologiaapp.paciente.domain.model.UsuarioPaciente
import dam2.tfg.psicologiaapp.psicologo.domain.model.UsuarioPsicologo
import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioModeloTest {

    @Test
    fun `nombreCompleto concatena nombre y apellidos en UsuarioPsicologo`() {
        val usuario = UsuarioPsicologo(
            usuarioId = 1L,
            firebaseUid = "uid-psi",
            nombre = "Carlos",
            apellidos = "Ruiz García",
            fotoPerfilUrl = null,
            numeroColegiado = "12345",
            especialidades = emptyList(),
        )

        assertEquals("Carlos Ruiz García", usuario.nombreCompleto())
    }

    @Test
    fun `nombreCompleto concatena nombre y apellidos en UsuarioPaciente`() {
        val usuario = UsuarioPaciente(
            usuarioId = 2L,
            firebaseUid = "uid-pac",
            nombre = "Ana",
            apellidos = "López",
            fotoPerfilUrl = null,
            psicologoId = 10L,
            idPaciente = 100L,
        )

        assertEquals("Ana López", usuario.nombreCompleto())
    }

    @Test
    fun `nombreCompleto omite partes en blanco en UsuarioSinRol`() {
        val usuario = UsuarioSinRol(
            usuarioId = 3L,
            firebaseUid = "uid",
            nombre = "",
            apellidos = "Solo Apellidos",
            fotoPerfilUrl = null,
        )

        assertEquals("Solo Apellidos", usuario.nombreCompleto())
    }

    @Test
    fun `nombreCompleto devuelve cadena vacia si nombre y apellidos estan vacios`() {
        val usuario = UsuarioSinRol(
            usuarioId = 4L,
            firebaseUid = "uid",
            nombre = "",
            apellidos = "",
            fotoPerfilUrl = null,
        )

        assertEquals("", usuario.nombreCompleto())
    }
}
