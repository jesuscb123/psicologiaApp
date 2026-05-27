package dam2.tfg.psicologiaapp.nota.data.mappers

import dam2.tfg.psicologiaapp.nota.data.local.NotaEntity
import dam2.tfg.psicologiaapp.nota.data.remote.NotaResponseDto
import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import org.junit.Assert.assertEquals
import org.junit.Test

class NotaMappersTest {

    private val paciente = PacienteResponseDto(
        id = 10L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        fotoPerfilUrl = null,
        rol = "PACIENTE",
        psicologoId = 200L,
        idPaciente = 100L,
    )

    private val psicologo = PsicologoResponseDto(
        id = 20L,
        idEntidadPsicologo = 200L,
        firebaseUid = "uid-psi",
        nombre = "Carlos",
        apellidos = "Ruiz",
        fotoPerfilUrl = null,
        rol = "PSICOLOGO",
        numeroColegiado = "12345",
        especialidades = listOf("Clínica"),
        descripcion = null,
    )

    private val dto = NotaResponseDto(
        id = 1L,
        asunto = "Sesión 1",
        descripcion = "Notas de la sesión",
        ultimaModificacion = "2026-05-27T10:00:00+02:00",
        paciente = paciente,
        psicologo = psicologo,
    )

    private val domain = Nota(
        id = 1L,
        asunto = "Sesión 1",
        descripcion = "Notas de la sesión",
        ultimaModificacion = "2026-05-27T10:00:00+02:00",
        pacienteId = 100L,
        psicologoId = 200L,
    )

    @Test
    fun `NotaResponseDto toDomain mapea campos anidados`() {
        assertEquals(domain, dto.toDomain())
    }

    @Test
    fun `NotaResponseDto toEntity mapea ids de paciente y psicologo`() {
        val entity = dto.toEntity()
        assertEquals(
            NotaEntity(
                id = 1L,
                asunto = "Sesión 1",
                descripcion = "Notas de la sesión",
                ultimaModificacion = "2026-05-27T10:00:00+02:00",
                pacienteId = 100L,
                psicologoId = 200L,
            ),
            entity,
        )
    }

    @Test
    fun `NotaEntity toDomain mapea todos los campos`() {
        val entity = NotaEntity(
            id = 1L,
            asunto = "Sesión 1",
            descripcion = "Notas de la sesión",
            ultimaModificacion = "2026-05-27T10:00:00+02:00",
            pacienteId = 100L,
            psicologoId = 200L,
        )
        assertEquals(domain, entity.toDomain())
    }

    @Test
    fun `round-trip dto toEntity toDomain conserva dominio`() {
        assertEquals(dto.toDomain(), dto.toEntity().toDomain())
    }
}
