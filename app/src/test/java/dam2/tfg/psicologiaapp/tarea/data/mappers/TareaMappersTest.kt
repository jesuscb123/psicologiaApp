package dam2.tfg.psicologiaapp.tarea.data.mappers

import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.tarea.data.local.TareaEntity
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaResponseDto
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class TareaMappersTest {

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
        especialidades = emptyList(),
        descripcion = null,
    )

    private val dto = TareaResponseDto(
        id = 1L,
        titulo = "Ejercicio de respiración",
        descripcion = "Practicar 5 minutos",
        horaEnvio = "2026-05-27T08:00:00+02:00",
        realizada = false,
        aceptadaPorPaciente = true,
        psicologo = psicologo,
        paciente = paciente,
    )

    private val domain = Tarea(
        id = 1L,
        titulo = "Ejercicio de respiración",
        descripcion = "Practicar 5 minutos",
        horaEnvio = "2026-05-27T08:00:00+02:00",
        realizada = false,
        aceptadaPorPaciente = true,
        psicologoId = 200L,
        pacienteId = 100L,
    )

    @Test
    fun `TareaResponseDto toDomain mapea campos y relaciones`() {
        assertEquals(domain, dto.toDomain())
    }

    @Test
    fun `TareaResponseDto toDomain usa false si aceptadaPorPaciente es null`() {
        val dtoSinAceptacion = dto.copy(aceptadaPorPaciente = null)

        assertFalse(dtoSinAceptacion.toDomain().aceptadaPorPaciente)
    }

    @Test
    fun `TareaResponseDto toEntity mapea todos los campos`() {
        assertEquals(
            TareaEntity(
                id = 1L,
                titulo = "Ejercicio de respiración",
                descripcion = "Practicar 5 minutos",
                horaEnvio = "2026-05-27T08:00:00+02:00",
                realizada = false,
                aceptadaPorPaciente = true,
                psicologoId = 200L,
                pacienteId = 100L,
            ),
            dto.toEntity(),
        )
    }

    @Test
    fun `TareaEntity toDomain mapea todos los campos`() {
        val entity = TareaEntity(
            id = 1L,
            titulo = "Ejercicio de respiración",
            descripcion = "Practicar 5 minutos",
            horaEnvio = "2026-05-27T08:00:00+02:00",
            realizada = false,
            aceptadaPorPaciente = true,
            psicologoId = 200L,
            pacienteId = 100L,
        )
        assertEquals(domain, entity.toDomain())
    }

    @Test
    fun `round-trip dto toEntity toDomain conserva dominio`() {
        assertEquals(dto.toDomain(), dto.toEntity().toDomain())
    }
}
