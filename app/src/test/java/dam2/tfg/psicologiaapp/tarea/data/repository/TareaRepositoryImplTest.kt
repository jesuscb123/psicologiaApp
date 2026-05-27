package dam2.tfg.psicologiaapp.tarea.data.repository

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import app.cash.turbine.test
import dam2.tfg.psicologiaapp.data.remote.EstadoSyncResponseDto
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoResponseDto
import dam2.tfg.psicologiaapp.tarea.data.local.TareaDao
import dam2.tfg.psicologiaapp.tarea.data.local.TareaEntity
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaActualizarRealizadaRequestDto
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaActualizarRequestDto
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaApi
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaCrearRequestDto
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaResponseDto
import dam2.tfg.psicologiaapp.test.FakeDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
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

class TareaRepositoryImplTest {

    private val api = FakeTareaApi()
    private val proveedorToken = mock<ProveedorTokenFirebase>()
    private lateinit var dao: FakeTareaDao
    private lateinit var dataStore: FakeDataStore
    private lateinit var repository: TareaRepositoryImpl

    @Before
    fun setUp() {
        dao = FakeTareaDao()
        dataStore = FakeDataStore()
        repository = TareaRepositoryImpl(api, proveedorToken, dao, dataStore)
    }

    @Test
    fun `getTareasPacienteActual debe sincronizar y devolver tareas del dao`() = runTest {
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T10:00:00", total = 1),
        )
        api.tareasPacienteActual = Response.success(listOf(tareaDto))

        val resultado = repository.getTareasPacienteActual()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
        assertEquals(1, dao.entidades.size)
    }

    @Test
    fun `getTareasPacienteActual no debe descargar lista si el estado no cambio`() = runTest {
        dataStore.edit {
            it[stringPreferencesKey("sync_tareas_paciente_actual_ultima_modificacion")] = "2026-05-27T10:00:00"
            it[longPreferencesKey("sync_tareas_paciente_actual_total")] = 1L
        }
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T10:00:00", total = 1),
        )
        dao.entidades.add(tareaEntity)

        repository.getTareasPacienteActual()

        assertEquals(0, api.llamadasTareasPacienteActual)
    }

    @Test
    fun `crearTarea debe guardar en dao`() = runTest {
        api.tareaCreada = tareaDto.copy(id = 2L, titulo = "Nueva tarea")
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T11:00:00", total = 2),
        )

        val resultado = repository.crearTarea(100L, "Nueva tarea", "Desc")

        assertTrue(resultado.isSuccess)
        assertEquals("Nueva tarea", resultado.getOrNull()?.titulo)
        assertTrue(dao.entidades.any { it.id == 2L })
        assertEquals(TareaCrearRequestDto("Nueva tarea", "Desc"), api.ultimaCreacion)
        assertEquals(100L, api.ultimoPacienteCreacion)
    }

    @Test
    fun `marcarRealizada debe actualizar dao`() = runTest {
        dao.entidades.add(tareaEntity)
        api.tareaActualizada = tareaDto.copy(realizada = true)
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T12:00:00", total = 1),
        )

        val resultado = repository.marcarRealizada(1L, true)

        assertTrue(resultado.isSuccess)
        assertTrue(dao.entidades.first().realizada)
        assertEquals(TareaActualizarRealizadaRequestDto(true), api.ultimaRealizada)
    }

    @Test
    fun `aceptarTarea debe actualizar dao`() = runTest {
        api.tareaActualizada = tareaDto.copy(aceptadaPorPaciente = true)
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T12:00:00", total = 1),
        )

        val resultado = repository.aceptarTarea(1L)

        assertTrue(resultado.isSuccess)
        assertEquals(1L, api.ultimoAceptarId)
    }

    @Test
    fun `actualizarTarea debe delegar en la API y persistir`() = runTest {
        api.tareaActualizada = tareaDto.copy(titulo = "Editada")
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T12:00:00", total = 1),
        )

        val resultado = repository.actualizarTarea(1L, "Editada", "Desc", false)

        assertTrue(resultado.isSuccess)
        assertEquals(
            TareaActualizarRequestDto("Editada", "Desc", false),
            api.ultimaActualizacion,
        )
    }

    @Test
    fun `eliminarTarea debe borrar del dao`() = runTest {
        dao.entidades.add(tareaEntity)
        api.estadoPacienteActual = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T12:00:00", total = 0),
        )

        val resultado = repository.eliminarTarea(1L)

        assertTrue(resultado.isSuccess)
        assertTrue(dao.entidades.isEmpty())
        assertEquals(1L, api.ultimoEliminarId)
    }

    @Test
    fun `getTareasDePaciente debe sincronizar por pacienteId`() = runTest {
        api.estadoDePaciente[100L] = Response.success(
            EstadoSyncResponseDto(ultimaModificacion = "2026-05-27T10:00:00", total = 1),
        )
        api.tareasDePaciente[100L] = Response.success(listOf(tareaDto))

        val resultado = repository.getTareasDePaciente(100L)

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
    }

    @Test
    fun `sincronizarTareasPacienteActual debe reintentar tras 401`() = runTest {
        api.estadoPacienteActualSecuencia = listOf(
            Response.error(401, "".toResponseBody(null)),
            Response.success(EstadoSyncResponseDto("2026-05-27T10:00:00", 0)),
        )
        api.tareasPacienteActual = Response.success(204, null as List<TareaResponseDto>?)
        whenever(proveedorToken.obtenerToken(forzarRenovacion = true)).thenReturn("token")

        val resultado = repository.sincronizarTareasPacienteActual()

        assertTrue(resultado.isSuccess)
        verify(proveedorToken).obtenerToken(forzarRenovacion = true)
    }

    @Test
    fun `observarTareasPacienteActual debe emitir datos del dao`() = runTest {
        dao.entidades.add(tareaEntity)
        dao.emitirTodas()

        repository.observarTareasPacienteActual().test {
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

    private val tareaDto = TareaResponseDto(
        id = 1L,
        titulo = "Tarea",
        descripcion = "Desc",
        horaEnvio = "2026-05-27T10:00:00",
        realizada = false,
        aceptadaPorPaciente = false,
        psicologo = psicologoDto,
        paciente = pacienteDto,
    )

    private val tareaEntity = TareaEntity(
        id = 1L,
        titulo = "Tarea",
        descripcion = "Desc",
        horaEnvio = "2026-05-27T10:00:00",
        realizada = false,
        aceptadaPorPaciente = false,
        psicologoId = 200L,
        pacienteId = 100L,
    )

    private class FakeTareaDao : TareaDao {
        val entidades = mutableListOf<TareaEntity>()
        private val stateTodas = MutableStateFlow<List<TareaEntity>>(emptyList())
        private val statePorPaciente = mutableMapOf<Long, MutableStateFlow<List<TareaEntity>>>()

        fun emitirTodas() {
            stateTodas.value = entidades.toList()
        }

        override fun observarTodas(): Flow<List<TareaEntity>> = stateTodas

        override fun observarPorPacienteId(pacienteId: Long): Flow<List<TareaEntity>> =
            statePorPaciente.getOrPut(pacienteId) { MutableStateFlow(emptyList()) }

        override suspend fun listarTodas(): List<TareaEntity> = entidades.toList()

        override suspend fun listarPorPacienteId(pacienteId: Long): List<TareaEntity> =
            entidades.filter { it.pacienteId == pacienteId }

        override suspend fun obtenerPorId(id: Long): TareaEntity? =
            entidades.firstOrNull { it.id == id }

        override suspend fun guardar(tarea: TareaEntity) {
            entidades.removeAll { it.id == tarea.id }
            entidades.add(tarea)
            emitirTodas()
        }

        override suspend fun guardarTodas(tareas: List<TareaEntity>) {
            tareas.forEach { guardar(it) }
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

    private class FakeTareaApi : TareaApi {
        var estadoPacienteActual: Response<EstadoSyncResponseDto> =
            Response.success(EstadoSyncResponseDto(null, 0))
        var estadoPacienteActualSecuencia: List<Response<EstadoSyncResponseDto>>? = null
        var tareasPacienteActual: Response<List<TareaResponseDto>> = Response.success(emptyList())
        var tareaCreada: TareaResponseDto = tareaResponseVacio()
        var tareaActualizada: TareaResponseDto = tareaResponseVacio()
        val estadoDePaciente = mutableMapOf<Long, Response<EstadoSyncResponseDto>>()
        val tareasDePaciente = mutableMapOf<Long, Response<List<TareaResponseDto>>>()
        var llamadasEstadoPacienteActual = 0
        var llamadasTareasPacienteActual = 0
        var ultimaCreacion: TareaCrearRequestDto? = null
        var ultimoPacienteCreacion: Long? = null
        var ultimaRealizada: TareaActualizarRealizadaRequestDto? = null
        var ultimoAceptarId: Long? = null
        var ultimaActualizacion: TareaActualizarRequestDto? = null
        var ultimoEliminarId: Long? = null

        override suspend fun getTareasPacienteActual(): Response<List<TareaResponseDto>> {
            llamadasTareasPacienteActual++
            return tareasPacienteActual
        }

        override suspend fun getEstadoTareasPacienteActual(): Response<EstadoSyncResponseDto> {
            llamadasEstadoPacienteActual++
            return estadoPacienteActualSecuencia
                ?.getOrNull(llamadasEstadoPacienteActual - 1)
                ?: estadoPacienteActual
        }

        override suspend fun getTareasDePaciente(pacienteId: Long): Response<List<TareaResponseDto>> =
            tareasDePaciente[pacienteId] ?: Response.success(emptyList())

        override suspend fun getEstadoTareasDePaciente(pacienteId: Long): Response<EstadoSyncResponseDto> =
            estadoDePaciente[pacienteId] ?: Response.success(EstadoSyncResponseDto(null, 0))

        override suspend fun crearTarea(pacienteId: Long, body: TareaCrearRequestDto): TareaResponseDto {
            ultimoPacienteCreacion = pacienteId
            ultimaCreacion = body
            return tareaCreada
        }

        override suspend fun marcarRealizada(
            tareaId: Long,
            body: TareaActualizarRealizadaRequestDto,
        ): TareaResponseDto {
            ultimaRealizada = body
            return tareaActualizada.copy(id = tareaId)
        }

        override suspend fun aceptarTarea(tareaId: Long): TareaResponseDto {
            ultimoAceptarId = tareaId
            return tareaActualizada.copy(id = tareaId)
        }

        override suspend fun actualizarTarea(
            tareaId: Long,
            body: TareaActualizarRequestDto,
        ): TareaResponseDto {
            ultimaActualizacion = body
            return tareaActualizada.copy(id = tareaId)
        }

        override suspend fun eliminarTarea(tareaId: Long) {
            ultimoEliminarId = tareaId
        }

        companion object {
            private fun tareaResponseVacio() = TareaResponseDto(
                id = 0L,
                titulo = "",
                descripcion = "",
                horaEnvio = "",
                realizada = false,
                psicologo = PsicologoResponseDto(
                    0L, 0L, "", "", "", null, "PSICOLOGO", "", emptyList(),
                ),
                paciente = PacienteResponseDto(
                    0L, "", "", "", null, "PACIENTE", null, 0L,
                ),
            )
        }
    }
}
