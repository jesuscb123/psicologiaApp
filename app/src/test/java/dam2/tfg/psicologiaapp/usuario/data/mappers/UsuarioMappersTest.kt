package dam2.tfg.psicologiaapp.usuario.data.mappers

import dam2.tfg.psicologiaapp.paciente.data.remote.PacientePerfilResponseDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteRequestDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.paciente.domain.model.PacienteRequest
import dam2.tfg.psicologiaapp.paciente.domain.model.UsuarioPaciente
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoPerfilResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoRequest
import dam2.tfg.psicologiaapp.psicologo.domain.model.UsuarioPsicologo
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilBasicoResponseDto
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioSinRol
import org.junit.Assert.assertEquals
import org.junit.Test

class UsuarioMappersTest {

    @Test
    fun `PsicologoResponseDto toDomain mapea a UsuarioPsicologo`() {
        val dto = PsicologoResponseDto(
            id = 1L,
            idEntidadPsicologo = 10L,
            firebaseUid = "uid-psi",
            nombre = "Carlos",
            apellidos = "Ruiz",
            fotoPerfilUrl = null,
            rol = "PSICOLOGO",
            numeroColegiado = "12345",
            especialidades = listOf("Clínica"),
            descripcion = "Desc",
        )

        assertEquals(
            UsuarioPsicologo(
                usuarioId = 1L,
                firebaseUid = "uid-psi",
                nombre = "Carlos",
                apellidos = "Ruiz",
                fotoPerfilUrl = null,
                rol = RolUsuario.PSICOLOGO,
                numeroColegiado = "12345",
                especialidades = listOf("Clínica"),
                descripcion = "Desc",
            ),
            dto.toDomain(),
        )
    }

    @Test
    fun `PacienteResponseDto toDomain mapea a UsuarioPaciente`() {
        val dto = PacienteResponseDto(
            id = 2L,
            firebaseUid = "uid-pac",
            nombre = "Ana",
            apellidos = "López",
            fotoPerfilUrl = null,
            rol = "PACIENTE",
            psicologoId = 10L,
            idPaciente = 100L,
        )

        assertEquals(
            UsuarioPaciente(
                usuarioId = 2L,
                firebaseUid = "uid-pac",
                nombre = "Ana",
                apellidos = "López",
                fotoPerfilUrl = null,
                rol = RolUsuario.PACIENTE,
                psicologoId = 10L,
                idPaciente = 100L,
            ),
            dto.toDomain(),
        )
    }

    @Test
    fun `UsuarioBasicoResponseDto toDomain mapea a UsuarioSinRol`() {
        val dto = UsuarioBasicoResponseDto(
            id = 3L,
            firebaseUid = "uid",
            nombre = "Sin",
            apellidos = "Rol",
            fotoPerfilUrl = null,
            rol = "SIN_ROL",
        )

        assertEquals(
            UsuarioSinRol(
                usuarioId = 3L,
                firebaseUid = "uid",
                nombre = "Sin",
                apellidos = "Rol",
                fotoPerfilUrl = null,
                rol = RolUsuario.SIN_ROL,
            ),
            dto.toDomain(),
        )
    }

    @Test
    fun `rol desconocido en response se mapea a SIN_ROL`() {
        val dto = UsuarioBasicoResponseDto(
            id = 4L,
            firebaseUid = "uid",
            nombre = "Test",
            apellidos = "User",
            fotoPerfilUrl = null,
            rol = "ADMIN",
        )

        assertEquals(RolUsuario.SIN_ROL, dto.toDomain().rol)
    }

    @Test
    fun `PsicologoPerfilResponseDto toDomain mapea perfil de psicologo`() {
        val dto = PsicologoPerfilResponseDto(
            id = 1L,
            firebaseUid = "uid-psi",
            nombre = "Carlos",
            apellidos = "Ruiz",
            email = "carlos@test.com",
            fotoPerfilUrl = null,
            rol = "PSICOLOGO",
            numeroColegiado = "12345",
            especialidades = listOf("Clínica"),
            descripcion = null,
        )

        assertEquals(
            PsicologoPerfil(
                usuarioId = 1L,
                firebaseUid = "uid-psi",
                nombre = "Carlos",
                apellidos = "Ruiz",
                email = "carlos@test.com",
                fotoPerfilUrl = null,
                rol = RolUsuario.PSICOLOGO,
                numeroColegiado = "12345",
                especialidades = listOf("Clínica"),
                descripcion = null,
            ),
            dto.toDomain(),
        )
    }

    @Test
    fun `PacientePerfilResponseDto toDomain mapea perfil de paciente`() {
        val dto = PacientePerfilResponseDto(
            id = 2L,
            firebaseUid = "uid-pac",
            nombre = "Ana",
            apellidos = "López",
            email = "ana@test.com",
            fotoPerfilUrl = null,
            rol = "PACIENTE",
            psicologoId = 10L,
        )

        assertEquals(
            PacientePerfil(
                usuarioId = 2L,
                firebaseUid = "uid-pac",
                nombre = "Ana",
                apellidos = "López",
                email = "ana@test.com",
                fotoPerfilUrl = null,
                rol = RolUsuario.PACIENTE,
                psicologoId = 10L,
            ),
            dto.toDomain(),
        )
    }

    @Test
    fun `UsuarioPerfilBasicoResponseDto toDomain mapea perfil basico`() {
        val dto = UsuarioPerfilBasicoResponseDto(
            id = 3L,
            firebaseUid = "uid",
            nombre = "Basico",
            apellidos = "User",
            email = "b@test.com",
            fotoPerfilUrl = null,
            rol = "SIN_ROL",
        )

        assertEquals(
            UsuarioPerfilBasico(
                usuarioId = 3L,
                firebaseUid = "uid",
                nombre = "Basico",
                apellidos = "User",
                email = "b@test.com",
                fotoPerfilUrl = null,
                rol = RolUsuario.SIN_ROL,
            ),
            dto.toDomain(),
        )
    }

    @Test
    fun `PsicologoRequest toDto mapea campos`() {
        val request = PsicologoRequest(
            nombre = "Carlos",
            apellidos = "Ruiz",
            fotoPerfilUrl = "url",
            numeroColegiado = "12345",
            especialidades = listOf("Clínica"),
            descripcion = "Desc",
        )

        assertEquals(
            PsicologoRequestDto(
                nombre = "Carlos",
                apellidos = "Ruiz",
                fotoPerfilUrl = "url",
                numeroColegiado = "12345",
                especialidades = listOf("Clínica"),
                descripcion = "Desc",
            ),
            request.toDto(),
        )
    }

    @Test
    fun `PacienteRequest toDto mapea campos`() {
        val request = PacienteRequest(
            nombre = "Ana",
            apellidos = "López",
            fotoPerfilUrl = null,
            psicologoId = 10L,
        )

        assertEquals(
            PacienteRequestDto(
                nombre = "Ana",
                apellidos = "López",
                fotoPerfilUrl = null,
                psicologoId = 10L,
            ),
            request.toDto(),
        )
    }
}
