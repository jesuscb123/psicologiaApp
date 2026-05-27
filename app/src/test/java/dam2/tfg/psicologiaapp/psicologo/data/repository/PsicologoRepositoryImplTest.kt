package dam2.tfg.psicologiaapp.psicologo.data.repository

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteEntity
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoDao
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoEntity
import dam2.tfg.psicologiaapp.psicologo.data.remote.ActualizarDescripcionPsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.ActualizarEspecialidadesPsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoApi
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

class PsicologoRepositoryImplTest {

    private val api = FakePsicologoApi()
    private lateinit var psicologoDao: FakePsicologoDao
    private lateinit var pacienteDao: FakePacienteDao
    private lateinit var repository: PsicologoRepositoryImpl

    @Before
    fun setUp() {
        psicologoDao = FakePsicologoDao()
        pacienteDao = FakePacienteDao()
        repository = PsicologoRepositoryImpl(api, psicologoDao, pacienteDao)
    }

    @Test
    fun `listarPsicologos debe sincronizar API con Room`() = runTest {
        api.psicologos = listOf(psicologoDto)

        val resultado = repository.listarPsicologos()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
        assertEquals(1, psicologoDao.entidades.size)
    }

    @Test
    fun `buscarPsicologos debe devolver resultados sin persistir`() = runTest {
        api.psicologosBusqueda = listOf(psicologoDto)

        val resultado = repository.buscarPsicologos("Carlos")

        assertTrue(resultado.isSuccess)
        assertTrue(psicologoDao.entidades.isEmpty())
        assertEquals("Carlos", api.ultimaBusqueda)
    }

    @Test
    fun `getPacientesDePsicologo debe sincronizar pacientes en Room`() = runTest {
        api.respuestaPacientes = Response.success(listOf(pacienteDto))

        val resultado = repository.getPacientesDePsicologo()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
        assertEquals(1, pacienteDao.entidades.size)
    }

    @Test
    fun `getPacientesDePsicologo con 204 debe devolver lista vacia y limpiar Room`() = runTest {
        pacienteDao.entidades.add(
            PacienteEntity(
                idPaciente = 1L,
                usuarioId = 1L,
                psicologoId = null,
            ),
        )
        api.respuestaPacientes = Response.success(204, null as List<PacienteResponseDto>?)

        val resultado = repository.getPacientesDePsicologo()

        assertTrue(resultado.isSuccess)
        assertTrue(resultado.getOrNull().isNullOrEmpty())
        assertTrue(pacienteDao.entidades.isEmpty())
    }

    @Test
    fun `getPacientesDePsicologo debe propagar error HTTP`() = runTest {
        api.respuestaPacientes = Response.error(500, "".toResponseBody(null))

        val resultado = repository.getPacientesDePsicologo()

        assertTrue(resultado.isFailure)
        assertTrue(resultado.exceptionOrNull()?.message?.contains("HTTP 500") == true)
    }

    @Test
    fun `actualizarMiDescripcion debe delegar en la API`() = runTest {
        api.psicologoActualizado = psicologoDto.copy(descripcion = "Nueva desc")

        val resultado = repository.actualizarMiDescripcion("Nueva desc")

        assertTrue(resultado.isSuccess)
        assertEquals("Nueva desc", resultado.getOrNull()?.descripcion)
        assertEquals(
            ActualizarDescripcionPsicologoRequestDto("Nueva desc"),
            api.ultimaDescripcion,
        )
    }

    @Test
    fun `actualizarMisEspecialidades debe delegar en la API`() = runTest {
        api.psicologoActualizado = psicologoDto.copy(especialidades = listOf("Infantil"))

        val resultado = repository.actualizarMisEspecialidades(listOf("Infantil"))

        assertTrue(resultado.isSuccess)
        assertEquals(listOf("Infantil"), resultado.getOrNull()?.especialidades)
        assertEquals(
            ActualizarEspecialidadesPsicologoRequestDto(listOf("Infantil")),
            api.ultimaEspecialidades,
        )
    }

    @Test
    fun `observarPsicologos debe emitir datos del dao`() = runTest {
        psicologoDao.entidades.add(
            PsicologoEntity(
                usuarioId = 20L,
                numeroColegiado = "12345",
                especialidades = emptyList(),
                idEntidadPsicologo = 200L,
                nombre = "Carlos",
                apellidos = "Ruiz",
            ),
        )
        psicologoDao.emitir()

        repository.observarPsicologos().test {
            assertEquals(1, awaitItem().size)
            cancelAndIgnoreRemainingEvents()
        }
    }

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

    private class FakePsicologoDao : PsicologoDao {
        val entidades = mutableListOf<PsicologoEntity>()
        private val state = MutableStateFlow<List<PsicologoEntity>>(emptyList())

        fun emitir() {
            state.value = entidades.toList()
        }

        override suspend fun obtenerPorUsuarioId(usuarioId: Long): PsicologoEntity? =
            entidades.firstOrNull { it.usuarioId == usuarioId }

        override fun observarTodos(): Flow<List<PsicologoEntity>> = state

        override suspend fun guardar(psicologo: PsicologoEntity) {
            entidades.removeAll { it.usuarioId == psicologo.usuarioId }
            entidades.add(psicologo)
            emitir()
        }

        override suspend fun guardarTodos(psicologos: List<PsicologoEntity>) {
            entidades.clear()
            entidades.addAll(psicologos)
            emitir()
        }

        override suspend fun borrarTodos() {
            entidades.clear()
            emitir()
        }
    }

    private class FakePacienteDao : PacienteDao {
        val entidades = mutableListOf<PacienteEntity>()
        private val state = MutableStateFlow<List<PacienteEntity>>(emptyList())

        fun emitir() {
            state.value = entidades.toList()
        }

        override suspend fun obtenerPorId(idPaciente: Long): PacienteEntity? =
            entidades.firstOrNull { it.idPaciente == idPaciente }

        override fun observarPorId(idPaciente: Long): Flow<PacienteEntity?> =
            state.map { lista -> lista.firstOrNull { it.idPaciente == idPaciente } }

        override suspend fun obtenerPorUsuarioId(usuarioId: Long): PacienteEntity? =
            entidades.firstOrNull { it.usuarioId == usuarioId }

        override fun observarTodos(): Flow<List<PacienteEntity>> = state

        override suspend fun guardar(paciente: PacienteEntity) {
            entidades.removeAll { it.idPaciente == paciente.idPaciente }
            entidades.add(paciente)
            emitir()
        }

        override suspend fun guardarTodos(pacientes: List<PacienteEntity>) {
            entidades.clear()
            entidades.addAll(pacientes)
            emitir()
        }

        override suspend fun borrarTodos() {
            entidades.clear()
            emitir()
        }
    }

    private class FakePsicologoApi : PsicologoApi {
        var psicologos: List<PsicologoResponseDto> = emptyList()
        var psicologosBusqueda: List<PsicologoResponseDto> = emptyList()
        var respuestaPacientes: Response<List<PacienteResponseDto>> = Response.success(emptyList())
        var psicologoActualizado: PsicologoResponseDto = PsicologoResponseDto(
            id = 0L,
            idEntidadPsicologo = 0L,
            firebaseUid = "",
            nombre = "",
            apellidos = "",
            fotoPerfilUrl = null,
            rol = "PSICOLOGO",
            numeroColegiado = "",
            especialidades = emptyList(),
        )
        var ultimaBusqueda: String? = null
        var ultimaDescripcion: ActualizarDescripcionPsicologoRequestDto? = null
        var ultimaEspecialidades: ActualizarEspecialidadesPsicologoRequestDto? = null

        override suspend fun listarPsicologos(): List<PsicologoResponseDto> = psicologos

        override suspend fun buscarPsicologos(nombre: String): List<PsicologoResponseDto> {
            ultimaBusqueda = nombre
            return psicologosBusqueda
        }

        override suspend fun getPsicologoPorFirebase(firebaseId: String): PsicologoResponseDto =
            psicologoActualizado

        override suspend fun getPacientesDePsicologo(): Response<List<PacienteResponseDto>> =
            respuestaPacientes

        override suspend fun actualizarMiDescripcion(
            body: ActualizarDescripcionPsicologoRequestDto,
        ): PsicologoResponseDto {
            ultimaDescripcion = body
            return psicologoActualizado
        }

        override suspend fun actualizarMisEspecialidades(
            body: ActualizarEspecialidadesPsicologoRequestDto,
        ): PsicologoResponseDto {
            ultimaEspecialidades = body
            return psicologoActualizado
        }
    }
}
