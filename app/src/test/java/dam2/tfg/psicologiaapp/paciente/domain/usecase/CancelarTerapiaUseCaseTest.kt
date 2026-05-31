package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakePacienteRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CancelarTerapiaUseCaseTest {

    private val pacienteSinPsicologo = Paciente(
        usuarioId = 1L,
        firebaseUid = "uid-pac",
        nombre = "Ana",
        apellidos = "López",
        fotoPerfilUrl = null,
        psicologoId = null,
        idPaciente = 10L,
    )

    @Test
    fun `invoke ejecuta cancelacion y sincronizaciones en orden`() = runTest {
        val pasos = mutableListOf<String>()

        val pacienteRepo = object : FakePacienteRepository() {
            override suspend fun cancelarTerapia(): Result<Paciente> {
                pasos += "cancelarTerapia"
                return Result.success(pacienteSinPsicologo)
            }
        }
        val usuarioRepo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual(): Result<UsuarioPerfil> {
                pasos += "syncPerfil"
                return Result.success(
                    UsuarioPerfilBasico(1L, "uid-pac", "Ana", "López", "ana@test.com", null, RolUsuario.PACIENTE),
                )
            }
        }
        val cacheRepo = object : FakeUsuarioCacheRepository() {
            override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {
                pasos += "guardarCache"
            }
        }
        val notaRepo = object : FakeNotaRepository() {
            override suspend fun sincronizarNotasPacienteActual(): Result<Unit> {
                pasos += "syncNotas"
                return Result.success(Unit)
            }
        }
        val tareaRepo = object : FakeTareaRepository() {
            override suspend fun sincronizarTareasPacienteActual(): Result<Unit> {
                pasos += "syncTareas"
                return Result.success(Unit)
            }
        }
        val citaRepo = object : FakeCitaRepository() {
            override suspend fun sincronizarMisCitasPaciente(): Result<Unit> {
                pasos += "syncCitas"
                return Result.success(Unit)
            }
        }

        val useCase = CancelarTerapiaUseCase(
            pacienteRepository = pacienteRepo,
            sincronizarPerfilActualUseCase = SincronizarPerfilActualUseCase(usuarioRepo, cacheRepo),
            sincronizarNotasPacienteActualUseCase = SincronizarNotasPacienteActualUseCase(notaRepo),
            sincronizarTareasPacienteActualUseCase = SincronizarTareasPacienteActualUseCase(tareaRepo),
            sincronizarMisCitasPacienteUseCase = SincronizarMisCitasPacienteUseCase(citaRepo),
        )

        val resultado = useCase()

        assertTrue(resultado.isSuccess)
        assertEquals(
            listOf("cancelarTerapia", "syncPerfil", "guardarCache", "syncNotas", "syncTareas", "syncCitas"),
            pasos,
        )
    }

    @Test
    fun `invoke propaga fallo si cancelarTerapia falla`() = runTest {
        val pacienteRepo = object : FakePacienteRepository() {
            override suspend fun cancelarTerapia(): Result<Paciente> =
                Result.failure(Exception("Sin psicólogo asignado"))
        }

        val useCase = CancelarTerapiaUseCase(
            pacienteRepository = pacienteRepo,
            sincronizarPerfilActualUseCase = SincronizarPerfilActualUseCase(
                FakeUsuarioRepository(),
                FakeUsuarioCacheRepository(),
            ),
            sincronizarNotasPacienteActualUseCase = SincronizarNotasPacienteActualUseCase(FakeNotaRepository()),
            sincronizarTareasPacienteActualUseCase = SincronizarTareasPacienteActualUseCase(FakeTareaRepository()),
            sincronizarMisCitasPacienteUseCase = SincronizarMisCitasPacienteUseCase(FakeCitaRepository()),
        )

        val resultado = useCase()

        assertTrue(resultado.isFailure)
        assertEquals("Sin psicólogo asignado", resultado.exceptionOrNull()?.message)
    }

    private open class FakeUsuarioRepository : UsuarioRepository {
        override suspend fun existeCorreo(email: String): Result<Boolean> = Result.success(true)
        override suspend fun getPerfilActual(): Result<UsuarioPerfil> = Result.failure(NotImplementedError())
        override suspend fun crearUsuario(request: UsuarioRequest): Result<Usuario> = Result.failure(NotImplementedError())
        override suspend fun actualizarEmail(nuevoEmail: String): Result<UsuarioPerfil> = Result.failure(NotImplementedError())
        override suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<UsuarioPerfil> =
            Result.failure(NotImplementedError())
        override suspend fun borrarUsuario(): Result<Unit> = Result.success(Unit)
        override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): Result<Usuario> =
            Result.failure(NotImplementedError())
    }

    private open class FakeUsuarioCacheRepository : UsuarioCacheRepository {
        override suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado? = null
        override fun observarPerfilCacheado(): Flow<PerfilCacheado?> = emptyFlow()
        override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) = Unit
        override suspend fun limpiarCache() = Unit
    }
}
