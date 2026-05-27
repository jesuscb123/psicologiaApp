package dam2.tfg.psicologiaapp.nota.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import dam2.tfg.psicologiaapp.data.remote.EstadoSyncResponseDto
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.nota.data.local.NotaDao
import dam2.tfg.psicologiaapp.nota.data.local.NotaEntity
import dam2.tfg.psicologiaapp.nota.data.remote.NotaApi
import dam2.tfg.psicologiaapp.nota.data.remote.NotaRequestDto
import dam2.tfg.psicologiaapp.nota.data.remote.NotaResponseDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.test.FakeDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

class NotaRepositoryImplTest {

    private val api = FakeNotaApi()
    private val proveedorToken = mock<ProveedorTokenFirebase>()
    private lateinit var dao: FakeNotaDao
    private lateinit var dataStore: FakeDataStore
    private lateinit var repository: NotaRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeNotaDao()
        dataStore = FakeDataStore()
        repository = NotaRepositoryImpl(api, proveedorToken, dao, dataStore)
    }

    @Test
    fun `getNotasPacienteActual debe sincronizar y devolver notas del dao`() = runTest {
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T10:00:00", total = 1),
        )
        api.notasPacienteActual = Response.success(listOf(notaDto))

        val resultado = repository.getNotasPacienteActual()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
        assertEquals(1, dao.entidades.size)
    }

    @Test
    fun `getNotasPacienteActual no debe descargar lista si el estado no cambio`() = runTest {
        dataStore.edit {
            it[stringPreferencesKey("sync_notas_paciente_actual_ultima_modificacion")] = "2026-05-27T10:00:00"
            it[longPreferencesKey("sync_notas_paciente_actual_total")] = 1L
        }
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T10:00:00", total = 1),
        )
        dao.entidades.add(notaEntity)

        repository.getNotasPacienteActual()

        assertEquals(0, api.llamadasNotasPacienteActual)
    }

    @Test
    fun `getNotasPacienteActual con 204 debe limpiar dao`() = runTest {
        dao.entidades.add(notaEntity)
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T10:00:00", total = 0),
        )
        api.notasPacienteActual = Response.success(204, null as List<NotaResponseDto>?)

        val resultado = repository.getNotasPacienteActual()

        assertTrue(resultado.isSuccess)
        assertTrue(dao.entidades.isEmpty())
    }

    @Test
    fun `crearNota debe guardar en dao y actualizar estado`() = runTest {
        api.notaCreada = notaDto.copy(id = 2L, asunto = "Nueva")
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T11:00:00", total = 2),
        )

        val resultado = repository.crearNota("Nueva", "Desc")

        assertTrue(resultado.isSuccess)
        assertEquals("Nueva", resultado.getOrNull()?.asunto)
        assertTrue(dao.entidades.any { it.id == 2L })
        assertEquals(NotaRequestDto("Nueva", "Desc"), api.ultimaCreacion)
    }

    @Test
    fun `borrarNota debe eliminar del dao`() = runTest {
        dao.entidades.add(notaEntity)
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T12:00:00", total = 0),
        )

        val resultado = repository.borrarNota(1L)

        assertTrue(resultado.isSuccess)
        assertTrue(dao.entidades.isEmpty())
        assertEquals(1L, api.ultimoBorradoId)
    }

    @Test
    fun `getNotasDePaciente debe sincronizar por pacienteId`() = runTest {
        api.estadoDePaciente[100L] = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T10:00:00", total = 1),
        )
        api.notasDePaciente[100L] = Response.success(listOf(notaDto))

        val resultado = repository.getNotasDePaciente(100L)

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
    }

    @Test
    fun `sincronizarNotasPacienteActual debe reintentar tras 401`() = runTest {
        api.estadoPacienteActualSecuencia = listOf(
            Response.error(401, "".toResponseBody(null)),
            Response.success(EstadoSyncResponseDto("2026-05-27T10:00:00", 0)),
        )
        api.notasPacienteActual = Response.success(204, null as List<NotaResponseDto>?)
        whenever(proveedorToken.obtenerToken(forzarRenovacion = true)).thenReturn("token")

        val resultado = repository.sincronizarNotasPacienteActual()

        assertTrue(resultado.isSuccess)
        verify(proveedorToken).obtenerToken(forzarRenovacion = true)
    }

    @Test
    fun `getNotasPacienteActual debe propagar error de estado HTTP 403`() = runTest {
        api.estadoPacienteActual = Response.error(403, "".toResponseBody(null))

        val resultado = repository.getNotasPacienteActual()

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()?.message?.contains("HTTP 403") == true)
    }

    @Test
    fun `observarNotasPacienteActual debe emitir datos del dao`() = runTest {
        dao.entidades.add(notaEntity)
        dao.emitirTodas()

        repository.observarNotasPacienteActual().test {
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

    private val notaDto = NotaResponseDto(
        id = 1L,
        asunto = "Asunto",
        descripcion = "Descripcion",
        ultimaModificacion = "2026-05-27T10:00:00",
        paciente = pacienteDto,
        psicologo = psicologoDto,
    )

    private val notaEntity = NotaEntity(
        id = 1L,
        asunto = "Asunto",
        descripcion = "Descripcion",
        ultimaModificacion = "2026-05-27T10:00:00",
        pacienteId = 100L,
        psicologoId = 200L,
    )

    private class FakeNotaDao : NotaDao {
        val entidades = mutableListOf<NotaEntity>()
        private val stateTodas = MutableStateFlow<List<NotaEntity>>(emptyList())
        private val statePorPaciente = mutableMapOf<Long, MutableStateFlow<List<NotaEntity>>>()

        fun emitirTodas() {
            stateTodas.value = entidades.toList()
        }

        override fun observarTodas(): Flow<List<NotaEntity>> = stateTodas

        override fun observarPorPacienteId(pacienteId: Long): Flow<List<NotaEntity>> =
            statePorPaciente.getOrPut(pacienteId) { MutableStateFlow(emptyList()) }

        override suspend fun listarTodas(): List<NotaEntity> = entidades.toList()

        override suspend fun listarPorPacienteId(pacienteId: Long): List<NotaEntity> =
            entidades.filter { it.pacienteId == pacienteId }

        override suspend fun obtenerPorId(id: Long): NotaEntity? =
            entidades.firstOrNull { it.id == id }

        override suspend fun guardar(nota: NotaEntity) {
            entidades.removeAll { it.id == nota.id }
            entidades.add(nota)
            emitirTodas()
        }

        override suspend fun guardarTodas(notas: List<NotaEntity>) {
            notas.forEach { guardar(it) }
        }

        override suspend fun borrarPorId(id: Long) {
            entidades.removeAll { it.id == id }
            emitirTodas()
        }

        override suspend fun borrarPorPacienteId(pacienteId: Long) {
            entidades.removeAll { it.pacienteId == pacienteId }
            emitirTodas()
        }

        override suspend fun borrarTodas() {
            entidades.clear()
            emitirTodas()
        }
    }

    private class FakeNotaApi : NotaApi {
        var estadoPacienteActual: Response<EstadoSyncResponseDto> =
            Response.success(EstadoSyncResponseDto(null, 0))
        var estadoPacienteActualSecuencia: List<Response<EstadoSyncResponseDto>>? = null
        var notasPacienteActual: Response<List<NotaResponseDto>> = Response.success(emptyList())
        var notaCreada: NotaResponseDto = NotaResponseDto(
            0L, "", "", "",
            PacienteResponseDto(0L, "", "", "", null, "PACIENTE", null, 0L),
            PsicologoResponseDto(0L, 0L, "", "", "", null, "PSICOLOGO", "", emptyList()),
        )
        val estadoDePaciente = mutableMapOf<Long, Response<EstadoSyncResponseDto>>()
        val notasDePaciente = mutableMapOf<Long, Response<List<NotaResponseDto>>>()
        var llamadasEstadoPacienteActual = 0
        var llamadasNotasPacienteActual = 0
        var ultimaCreacion: NotaRequestDto? = null
        var ultimoBorradoId: Long? = null

        override suspend fun getNotasPacienteActual(): Response<List<NotaResponseDto>> {
            llamadasNotasPacienteActual++
            return notasPacienteActual
        }

        override suspend fun getEstadoNotasPacienteActual(): Response<EstadoSyncResponseDto> {
            llamadasEstadoPacienteActual++
            return estadoPacienteActualSecuencia
                ?.getOrNull(llamadasEstadoPacienteActual - 1)
                ?: estadoPacienteActual
        }

        override suspend fun getNotasDePaciente(pacienteId: Long): Response<List<NotaResponseDto>> =
            notasDePaciente[pacienteId] ?: Response.success(emptyList())

        override suspend fun getEstadoNotasDePaciente(pacienteId: Long): Response<EstadoSyncResponseDto> =
            estadoDePaciente[pacienteId] ?: Response.success(EstadoSyncResponseDto(null, 0))

        override suspend fun crearNota(body: NotaRequestDto): NotaResponseDto {
            ultimaCreacion = body
            return notaCreada
        }

        override suspend fun actualizarNota(notaId: Long, body: NotaRequestDto): NotaResponseDto =
            notaCreada.copy(id = notaId, asunto = body.asunto, descripcion = body.descripcion)

        override suspend fun borrarNota(notaId: Long) {
            ultimoBorradoId = notaId
        }
    }
}
