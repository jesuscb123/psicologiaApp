package dam2.tfg.psicologiaapp.paciente.data.mappers

import dam2.tfg.psicologiaapp.BuildConfig
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteEntity
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PacienteMappersTest {

    private val dto = PacienteResponseDto(
        id = 10L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        fotoPerfilUrl = "https://example.com/foto.jpg",
        rol = "PACIENTE",
        psicologoId = 200L,
        idPaciente = 100L,
    )

    private val domain = Paciente(
        usuarioId = 10L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        fotoPerfilUrl = "https://example.com/foto.jpg",
        psicologoId = 200L,
        idPaciente = 100L,
    )

    @Test
    fun `PacienteResponseDto toDomain mapea todos los campos`() {
        assertEquals(domain, dto.toDomain())
    }

    @Test
    fun `PacienteResponseDto toDomain normaliza url localhost en foto`() {
        val localhostUrl = "http://localhost:8080/api/usuarios/foto.jpg"
        val dtoLocal = dto.copy(fotoPerfilUrl = localhostUrl)
        val base = BuildConfig.BASE_URL.trimEnd('/')

        assertEquals("$base/api/usuarios/foto.jpg", dtoLocal.toDomain().fotoPerfilUrl)
    }

    @Test
    fun `PacienteResponseDto toEntity mapea ids correctamente`() {
        assertEquals(
            PacienteEntity(
                idPaciente = 100L,
                usuarioId = 10L,
                psicologoId = 200L,
                firebaseUid = "uid-pac",
                nombre = "Ana",
                apellidos = "López",
                fotoPerfilUrl = "https://example.com/foto.jpg",
            ),
            dto.toEntity(),
        )
    }

    @Test
    fun `PacienteEntity toDomain mapea todos los campos`() {
        val entity = PacienteEntity(
            idPaciente = 100L,
            usuarioId = 10L,
            psicologoId = 200L,
            firebaseUid = "uid-pac",
            nombre = "Ana",
            apellidos = "López",
            fotoPerfilUrl = "https://example.com/foto.jpg",
        )
        assertEquals(domain, entity.toDomain())
    }

    @Test
    fun `round-trip dto toEntity toDomain conserva dominio con url remota`() {
        assertEquals(dto.toDomain(), dto.toEntity().toDomain())
    }

    @Test
    fun `PacienteResponseDto toDomain devuelve null si fotoPerfilUrl es blank`() {
        assertNull(dto.copy(fotoPerfilUrl = "   ").toDomain().fotoPerfilUrl)
    }
}
