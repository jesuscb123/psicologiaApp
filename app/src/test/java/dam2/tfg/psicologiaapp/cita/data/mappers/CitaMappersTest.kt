package dam2.tfg.psicologiaapp.cita.data.mappers

import dam2.tfg.psicologiaapp.cita.data.local.CitaEntity
import dam2.tfg.psicologiaapp.cita.data.remote.CitaResponseDto
import dam2.tfg.psicologiaapp.cita.data.remote.DisponibilidadResponseDto
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaCalculado
import dam2.tfg.psicologiaapp.cita.domain.model.EstadoCitaPersistido
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class CitaMappersTest {

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

    private val citaDto = CitaResponseDto(
        id = 1L,
        inicio = "2026-04-15T09:00:00+02:00",
        fin = "2026-04-15T10:00:00+02:00",
        psicologo = psicologo,
        paciente = paciente,
        estadoPersistido = "RESERVADA",
        estadoCalculado = "ACTIVA",
    )

    @Test
    fun `DisponibilidadResponseDto toDomain parsea fecha y horas`() {
        val dto = DisponibilidadResponseDto(
            fecha = "2026-04-15",
            zonaHoraria = "Europe/Madrid",
            horasDisponibles = listOf("09:00", "10:30:00", "invalido"),
        )

        val domain = dto.toDomain()

        assertEquals(LocalDate.of(2026, 4, 15), domain.fecha)
        assertEquals("Europe/Madrid", domain.zonaHoraria)
        assertEquals(listOf(LocalTime.of(9, 0), LocalTime.of(10, 30)), domain.horasDisponibles)
    }

    @Test
    fun `CitaResponseDto toDomain concatena nombres y parsea estados`() {
        val domain = citaDto.toDomain()

        assertEquals(1L, domain.id)
        assertEquals(200L, domain.psicologoId)
        assertEquals(100L, domain.pacienteId)
        assertEquals("Carlos Ruiz", domain.nombrePsicologo)
        assertEquals("Ana López", domain.nombrePaciente)
        assertEquals(EstadoCitaPersistido.RESERVADA, domain.estadoPersistido)
        assertEquals(EstadoCitaCalculado.ACTIVA, domain.estadoCalculado)
    }

    @Test
    fun `CitaResponseDto toDomain usa valores por defecto si estado es invalido`() {
        val dto = citaDto.copy(estadoPersistido = "DESCONOCIDO", estadoCalculado = "OTRO")

        val domain = dto.toDomain()

        assertEquals(EstadoCitaPersistido.RESERVADA, domain.estadoPersistido)
        assertEquals(EstadoCitaCalculado.ACTIVA, domain.estadoCalculado)
    }

    @Test
    fun `CitaResponseDto toEntity incluye flag esDePaciente`() {
        val entity = citaDto.toEntity(esDePaciente = true)

        assertEquals(true, entity.esDePaciente)
        assertEquals("RESERVADA", entity.estadoPersistido)
        assertEquals("ACTIVA", entity.estadoCalculado)
        assertEquals("Carlos Ruiz", entity.nombrePsicologo)
    }

    @Test
    fun `CitaEntity toDomain parsea estados almacenados como String`() {
        val entity = CitaEntity(
            id = 1L,
            inicio = "2026-04-15T09:00:00+02:00",
            fin = "2026-04-15T10:00:00+02:00",
            psicologoId = 200L,
            pacienteId = 100L,
            nombrePsicologo = "Carlos Ruiz",
            nombrePaciente = "Ana López",
            estadoPersistido = "CANCELADA",
            estadoCalculado = "FINALIZADA",
            esDePaciente = false,
        )

        val domain = entity.toDomain()

        assertEquals(EstadoCitaPersistido.CANCELADA, domain.estadoPersistido)
        assertEquals(EstadoCitaCalculado.FINALIZADA, domain.estadoCalculado)
    }

    @Test
    fun `round-trip dto toEntity toDomain conserva datos de dominio`() {
        val roundTrip = citaDto.toEntity(esDePaciente = false).toDomain()
        assertEquals(citaDto.toDomain(), roundTrip)
    }

    @Test
    fun `CitaResponseDto toDomain omite apellidos en blanco al concatenar`() {
        val dto = citaDto.copy(
            paciente = paciente.copy(nombre = "Ana", apellidos = ""),
            psicologo = psicologo.copy(nombre = "", apellidos = "Ruiz"),
        )

        val domain = dto.toDomain()

        assertEquals("Ana", domain.nombrePaciente)
        assertEquals("Ruiz", domain.nombrePsicologo)
        assertTrue(domain.nombrePaciente.isNotBlank())
    }
}
