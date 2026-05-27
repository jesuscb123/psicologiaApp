package dam2.tfg.psicologiaapp.cita.data.repository

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.cita.data.local.CitaDao
import dam2.tfg.psicologiaapp.cita.data.local.CitaEntity
import dam2.tfg.psicologiaapp.cita.data.remote.CitaApi
import dam2.tfg.psicologiaapp.cita.data.remote.CitaCrearRequestDto
import dam2.tfg.psicologiaapp.cita.data.remote.CitaResponseDto
import dam2.tfg.psicologiaapp.cita.data.remote.DisponibilidadResponseDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.time.LocalDate

class CitaRepositoryImplTest {

    private val api = FakeCitaApi()
    private lateinit var dao: FakeCitaDao
    private lateinit var repository: CitaRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeCitaDao()
        repository = CitaRepositoryImpl(api, dao)
    }

    @Test
    fun `getDisponibilidadDia debe mapear respuesta exitosa`() = runTest {
        api.respuestaDisponibilidad = Response.success(
            DisponibilidadResponseDto(
                fecha = "2026-04-15",
                zonaHoraria = "Europe/Madrid",
                horasDisponibles = listOf("09:00"),
            ),
        )

        val resultado = repository.getDisponibilidadDia(
            fecha = LocalDate.of(2026, 4, 15),
            zonaHoraria = "Europe/Madrid",
        )

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.horasDisponibles?.size)
    }

    @Test
    fun `getDisponibilidadDia con 204 debe devolver dia vacio`() = runTest {
        api.respuestaDisponibilidad = Response.success(204, null as DisponibilidadResponseDto?)

        val resultado = repository.getDisponibilidadDia(
            fecha = LocalDate.of(2026, 4, 15),
            zonaHoraria = "Europe/Madrid",
        )

        assertTrue(resultado.isSuccess)
        assertTrue(resultado.getOrNull()?.horasDisponibles.isNullOrEmpty())
    }

    @Test
    fun `reservarCita debe persistir en Room y devolver dominio`() = runTest {
        api.respuestaReservar = Response.success(citaDto)

        val resultado = repository.reservarCita(
            inicioIsoOffset = "2026-04-15T09:00:00+02:00",
            zonaHoraria = "Europe/Madrid",
        )

        assertTrue(resultado.isSuccess)
        assertEquals(1L, resultado.getOrNull()?.id)
        assertEquals(1, dao.citasPaciente.size)
        assertEquals(CitaCrearRequestDto("2026-04-15T09:00:00+02:00", "Europe/Madrid"), api.ultimaReserva)
    }

    @Test
    fun `reservarCita debe propagar error 409`() = runTest {
        api.respuestaReservar = Response.error(
            409,
            "Ese horario ya está reservado".toResponseBody(null),
        )

        val resultado = repository.reservarCita("2026-04-15T09:00:00+02:00", "Europe/Madrid")

        assertTrue(resultado.isFailure)
        assertEquals("Ese horario ya está reservado", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `getMisCitasPaciente debe sincronizar Room`() = runTest {
        api.respuestaCitasPaciente = Response.success(listOf(citaDto))

        val resultado = repository.getMisCitasPaciente()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
        assertEquals(1, dao.citasPaciente.size)
    }

    @Test
    fun `getMisCitasPaciente con 204 debe limpiar citas de paciente`() = runTest {
        dao.citasPaciente.add(citaEntity(esDePaciente = true))
        api.respuestaCitasPaciente = Response.success(204, null as List<CitaResponseDto>?)

        val resultado = repository.getMisCitasPaciente()

        assertTrue(resultado.isSuccess)
        assertTrue(resultado.getOrNull().isNullOrEmpty())
        assertTrue(dao.citasPaciente.isEmpty())
    }

    @Test
    fun `cancelarCita debe actualizar Room para ambos roles`() = runTest {
        val cancelada = citaDto.copy(estadoPersistido = "CANCELADA", estadoCalculado = "CANCELADA")
        api.respuestaCancelar = Response.success(cancelada)

        val resultado = repository.cancelarCita(1L)

        assertTrue(resultado.isSuccess)
        assertEquals(1, dao.citasPaciente.size)
        assertEquals(1, dao.citasPsicologo.size)
        assertEquals("CANCELADA", dao.citasPaciente.first().estadoPersistido)
    }

    @Test
    fun `sincronizarMisCitasPaciente debe delegar en getMisCitasPaciente`() = runTest {
        api.respuestaCitasPaciente = Response.success(listOf(citaDto))

        val resultado = repository.sincronizarMisCitasPaciente()

        assertTrue(resultado.isSuccess)
        assertEquals(1, dao.citasPaciente.size)
    }

    @Test
    fun `observarMisCitasPaciente debe emitir citas del dao`() = runTest {
        dao.citasPaciente.add(citaEntity(esDePaciente = true))
        dao.emitirPaciente()

        repository.observarMisCitasPaciente().test {
            assertEquals(1L, awaitItem().first().id)
            cancelAndIgnoreRemainingEvents()
        }
    }

    private val pacienteDto = PacienteResponseDto(
        id = 10L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        fotoPerfilUrl = null,
        rol = "PACIENTE",
        psicologoId = 200L,
        idPaciente = 100L,
    )

    private val psicologoDto = PsicologoResponseDto(
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
        psicologo = psicologoDto,
        paciente = pacienteDto,
        estadoPersistido = "RESERVADA",
        estadoCalculado = "ACTIVA",
    )

    private fun citaEntity(esDePaciente: Boolean) = CitaEntity(
        id = 1L,
        inicio = "2026-04-15T09:00:00+02:00",
        fin = "2026-04-15T10:00:00+02:00",
        psicologoId = 200L,
        pacienteId = 100L,
        nombrePsicologo = "Carlos Ruiz",
        nombrePaciente = "Ana López",
        estadoPersistido = "RESERVADA",
        estadoCalculado = "ACTIVA",
        esDePaciente = esDePaciente,
    )

    private class FakeCitaDao : CitaDao {
        val citasPaciente = mutableListOf<CitaEntity>()
        val citasPsicologo = mutableListOf<CitaEntity>()
        private val statePaciente = MutableStateFlow<List<CitaEntity>>(emptyList())
        private val statePsicologo = MutableStateFlow<List<CitaEntity>>(emptyList())

        fun emitirPaciente() {
            statePaciente.value = citasPaciente.toList()
        }

        override fun observarCitasPaciente(): Flow<List<CitaEntity>> = statePaciente

        override fun observarCitasPsicologo(): Flow<List<CitaEntity>> = statePsicologo

        override suspend fun guardarTodas(citas: List<CitaEntity>) {
            citas.forEach { cita ->
                if (cita.esDePaciente) {
                    citasPaciente.removeAll { it.id == cita.id }
                    citasPaciente.add(cita)
                } else {
                    citasPsicologo.removeAll { it.id == cita.id }
                    citasPsicologo.add(cita)
                }
            }
            emitirPaciente()
            statePsicologo.value = citasPsicologo.toList()
        }

        override suspend fun borrarCitasPaciente() {
            citasPaciente.clear()
            emitirPaciente()
        }

        override suspend fun borrarCitasPsicologo() {
            citasPsicologo.clear()
            statePsicologo.value = emptyList()
        }

        override suspend fun borrarPorId(citaId: Long) {
            citasPaciente.removeAll { it.id == citaId }
            citasPsicologo.removeAll { it.id == citaId }
            emitirPaciente()
            statePsicologo.value = citasPsicologo.toList()
        }
    }

    private class FakeCitaApi : CitaApi {
        var respuestaDisponibilidad: Response<DisponibilidadResponseDto> =
            Response.success(DisponibilidadResponseDto("", "", emptyList()))
        var respuestaReservar: Response<CitaResponseDto> = Response.success(
            CitaResponseDto(
                0L, "", "",
                PsicologoResponseDto(0L, 0L, "", "", "", null, "PSICOLOGO", "", emptyList()),
                PacienteResponseDto(0L, "", "", "", null, "PACIENTE", null, 0L),
                "", "",
            ),
        )
        var respuestaCitasPaciente: Response<List<CitaResponseDto>> = Response.success(emptyList())
        var respuestaCancelar: Response<CitaResponseDto> = respuestaReservar
        var ultimaReserva: CitaCrearRequestDto? = null

        override suspend fun getDisponibilidadDia(
            fechaIso: String,
            zonaHoraria: String,
        ): Response<DisponibilidadResponseDto> = respuestaDisponibilidad

        override suspend fun reservarCita(body: CitaCrearRequestDto): Response<CitaResponseDto> {
            ultimaReserva = body
            return respuestaReservar
        }

        override suspend fun getMisCitasPaciente(): Response<List<CitaResponseDto>> =
            respuestaCitasPaciente

        override suspend fun getMisCitasPsicologo(): Response<List<CitaResponseDto>> =
            Response.success(emptyList())

        override suspend fun cancelarCita(citaId: Long): Response<CitaResponseDto> =
            respuestaCancelar
    }
}
