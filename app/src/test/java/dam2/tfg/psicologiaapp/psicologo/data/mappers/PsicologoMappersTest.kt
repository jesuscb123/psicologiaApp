package dam2.tfg.psicologiaapp.psicologo.data.mappers

import dam2.tfg.psicologiaapp.BuildConfig
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoEntity
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import org.junit.Assert.assertEquals
import org.junit.Test

class PsicologoMappersTest {

    private val dto = PsicologoResponseDto(
        id = 20L,
        idEntidadPsicologo = 200L,
        firebaseUid = "uid-psi",
        nombre = "Carlos",
        apellidos = "Ruiz",
        fotoPerfilUrl = "https://example.com/psi.jpg",
        rol = "PSICOLOGO",
        numeroColegiado = "12345",
        especialidades = listOf("Clínica", "Infantil"),
        descripcion = "Especialista en ansiedad",
    )

    private val domain = Psicologo(
        usuarioId = 20L,
        idEntidadPsicologo = 200L,
        firebaseUid = "uid-psi",
        nombre = "Carlos",
        apellidos = "Ruiz",
        fotoPerfilUrl = "https://example.com/psi.jpg",
        numeroColegiado = "12345",
        especialidades = listOf("Clínica", "Infantil"),
        descripcion = "Especialista en ansiedad",
    )

    @Test
    fun `PsicologoResponseDto toDomain mapea todos los campos`() {
        assertEquals(domain, dto.toDomain())
    }

    @Test
    fun `PsicologoResponseDto toDomain normaliza url localhost en foto`() {
        val localhostUrl = "http://127.0.0.1:8080/api/psicologos/foto.jpg"
        val dtoLocal = dto.copy(fotoPerfilUrl = localhostUrl)
        val base = BuildConfig.BASE_URL.trimEnd('/')

        assertEquals("$base/api/psicologos/foto.jpg", dtoLocal.toDomain().fotoPerfilUrl)
    }

    @Test
    fun `PsicologoResponseDto toEntity mapea todos los campos`() {
        assertEquals(
            PsicologoEntity(
                usuarioId = 20L,
                idEntidadPsicologo = 200L,
                firebaseUid = "uid-psi",
                nombre = "Carlos",
                apellidos = "Ruiz",
                fotoPerfilUrl = "https://example.com/psi.jpg",
                numeroColegiado = "12345",
                especialidades = listOf("Clínica", "Infantil"),
                descripcion = "Especialista en ansiedad",
            ),
            dto.toEntity(),
        )
    }

    @Test
    fun `PsicologoEntity toDomain normaliza url en entidad cacheada`() {
        val entity = PsicologoEntity(
            usuarioId = 20L,
            idEntidadPsicologo = 200L,
            firebaseUid = "uid-psi",
            nombre = "Carlos",
            apellidos = "Ruiz",
            fotoPerfilUrl = "http://10.0.2.2:8080/api/foto.jpg",
            numeroColegiado = "12345",
            especialidades = listOf("Clínica"),
            descripcion = null,
        )
        val base = BuildConfig.BASE_URL.trimEnd('/')

        assertEquals("$base/api/foto.jpg", entity.toDomain().fotoPerfilUrl)
    }

    @Test
    fun `round-trip dto toEntity toDomain conserva dominio con url remota`() {
        assertEquals(dto.toDomain(), dto.toEntity().toDomain())
    }
}
