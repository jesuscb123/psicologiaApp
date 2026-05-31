package dam2.tfg.psicologiaapp.paciente.data.repository

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.paciente.data.local.PacienteEntity
import dam2.tfg.psicologiaapp.paciente.data.remote.AsignarPsicologoRequestDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteApi
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteResponseDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class PacienteRepositoryImplTest {

    private val api = FakePacienteApi()
    private lateinit var dao: FakePacienteDao
    private lateinit var repository: PacienteRepositoryImpl

    @Before
    fun setUp() {
        dao = FakePacienteDao()
        repository = PacienteRepositoryImpl(api, dao)
    }

    @Test
    fun `listarPacientes debe sincronizar API con Room y devolver dominio`() = runTest {
        api.pacientes = listOf(pacienteDto)

        val resultado = repository.listarPacientes()

        assertTrue(resultado.isSuccess)
        assertEquals(1, resultado.getOrNull()?.size)
        assertEquals(100L, resultado.getOrNull()?.first()?.idPaciente)
        assertEquals(1, dao.entidades.size)
    }

    @Test
    fun `buscarPacientes debe devolver resultados sin persistir`() = runTest {
        api.pacientesBusqueda = listOf(pacienteDto)

        val resultado = repository.buscarPacientes("Ana")

        assertTrue(resultado.isSuccess)
        assertEquals("Ana", resultado.getOrNull()?.first()?.nombre)
        assertTrue(dao.entidades.isEmpty())
        assertEquals("Ana", api.ultimaBusqueda)
    }

    @Test
    fun `getPacientePorFirebase debe mapear respuesta`() = runTest {
        api.pacientePorFirebase = pacienteDto

        val resultado = repository.getPacientePorFirebase("uid-pac")

        assertTrue(resultado.isSuccess)
        assertEquals("uid-pac", resultado.getOrNull()?.firebaseUid)
    }

    @Test
    fun `asignarPsicologo debe delegar en la API`() = runTest {
        api.pacienteAsignado = pacienteDto.copy(psicologoId = 99L)

        val resultado = repository.asignarPsicologo(99L)

        assertTrue(resultado.isSuccess)
        assertEquals(99L, resultado.getOrNull()?.psicologoId)
        assertEquals(AsignarPsicologoRequestDto(99L), api.ultimaAsignacion)
    }

    @Test
    fun `listarPacientes debe propagar error de la API`() = runTest {
        api.errorListar = IllegalStateException("Error de red")

        val resultado = repository.listarPacientes()

        assertTrue(resultado.isFailure)
        assertEquals("Error de red", resultado.exceptionOrNull()?.message)
    }

    @Test
    fun `observarPacientes debe emitir datos del dao`() = runTest {
        dao.entidades.add(
            PacienteEntity(
                idPaciente = 100L,
                usuarioId = 10L,
                psicologoId = 200L,
                firebaseUid = "uid-pac",
                nombre = "Ana",
                apellidos = "López",
            ),
        )
        dao.emitir()

        repository.observarPacientes().test {
            val pacientes = awaitItem()
            assertEquals(1, pacientes.size)
            assertEquals("Ana", pacientes.first().nombre)
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

    private class FakePacienteApi : PacienteApi {
        var pacientes: List<PacienteResponseDto> = emptyList()
        var pacientesBusqueda: List<PacienteResponseDto> = emptyList()
        var pacientePorFirebase: PacienteResponseDto = PacienteResponseDto(
            id = 0L,
            firebaseUid = "",
            nombre = "",
            apellidos = "",
            fotoPerfilUrl = null,
            rol = "PACIENTE",
            psicologoId = null,
            idPaciente = 0L,
        )
        var pacienteAsignado: PacienteResponseDto = pacientePorFirebase
        var errorListar: Throwable? = null
        var ultimaBusqueda: String? = null
        var ultimaAsignacion: AsignarPsicologoRequestDto? = null

        override suspend fun listarPacientes(): List<PacienteResponseDto> {
            errorListar?.let { throw it }
            return pacientes
        }

        override suspend fun buscarPacientes(nombre: String): List<PacienteResponseDto> {
            ultimaBusqueda = nombre
            return pacientesBusqueda
        }

        override suspend fun getPacientePorFirebase(firebaseId: String): PacienteResponseDto =
            pacientePorFirebase

        override suspend fun asignarPsicologo(body: AsignarPsicologoRequestDto): PacienteResponseDto {
            ultimaAsignacion = body
            return pacienteAsignado
        }

        override suspend fun cancelarTerapia(): PacienteResponseDto =
            pacienteAsignado.copy(psicologoId = null)
    }
}
