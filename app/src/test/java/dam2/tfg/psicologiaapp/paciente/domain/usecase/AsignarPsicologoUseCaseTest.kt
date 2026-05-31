package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.nota.domain.usecase.SincronizarNotasPacienteActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.test.fakes.FakeCitaRepository
import dam2.tfg.psicologiaapp.test.fakes.FakePacienteRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioCacheRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import dam2.tfg.psicologiaapp.test.fakes.FakeNotaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AsignarPsicologoUseCaseTest {

    private val pacienteAsignado = Paciente(1L, "uid", "Ana", "Lopez", null, 2L, 10L)

    @Test
    fun `invoke ejecuta asignacion y sincronizaciones`() = runTest {
        val pasos = mutableListOf<String>()
        val repo = object : FakePacienteRepository() {
            override suspend fun asignarPsicologo(psicologoId: Long): Result<Paciente> {
                pasos += "asignar"
                return Result.success(pacienteAsignado)
            }
        }
        val usuarioRepo = object : FakeUsuarioRepository() {
            override suspend fun getPerfilActual() = Result.success(
                UsuarioPerfilBasico(1L, "uid", "Ana", "Lopez", "ana@test.com", null, RolUsuario.PACIENTE),
            )
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

        val resultado = AsignarPsicologoUseCase(
            pacienteRepository = repo,
            sincronizarPerfilActualUseCase = SincronizarPerfilActualUseCase(usuarioRepo, FakeUsuarioCacheRepository()),
            sincronizarNotasPacienteActualUseCase = SincronizarNotasPacienteActualUseCase(notaRepo),
            sincronizarTareasPacienteActualUseCase = SincronizarTareasPacienteActualUseCase(tareaRepo),
            sincronizarMisCitasPacienteUseCase = SincronizarMisCitasPacienteUseCase(citaRepo),
        )(psicologoId = 2L)

        assertTrue(resultado.isSuccess)
        assertEquals(listOf("asignar", "syncNotas", "syncTareas", "syncCitas"), pasos.filter { it != "guardarCache" && !it.startsWith("syncPerfil") })
        assertTrue(pasos.contains("asignar"))
        assertTrue(pasos.contains("syncNotas"))
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakePacienteRepository() {
            override suspend fun asignarPsicologo(psicologoId: Long) =
                Result.failure<Paciente>(Exception("Error de prueba"))
        }
        val resultado = AsignarPsicologoUseCase(
            pacienteRepository = repo,
            sincronizarPerfilActualUseCase = SincronizarPerfilActualUseCase(
                FakeUsuarioRepository(),
                FakeUsuarioCacheRepository(),
            ),
            sincronizarNotasPacienteActualUseCase = SincronizarNotasPacienteActualUseCase(FakeNotaRepository()),
            sincronizarTareasPacienteActualUseCase = SincronizarTareasPacienteActualUseCase(FakeTareaRepository()),
            sincronizarMisCitasPacienteUseCase = SincronizarMisCitasPacienteUseCase(FakeCitaRepository()),
        )(psicologoId = 2L)
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
