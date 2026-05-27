package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class MarcarTareaRealizadaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Tarea(1L, "Titulo", "Desc", "2024-01-01", false, true, 2L, 1L)
        val repo = object : FakeTareaRepository() {
            override suspend fun marcarRealizada(tareaId: Long, realizada: Boolean) = Result.success(expected)
        }
        val resultado = MarcarTareaRealizadaUseCase(repo)(tareaId = 1L, realizada = true)
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeTareaRepository() {
            override suspend fun marcarRealizada(tareaId: Long, realizada: Boolean) =
                Result.failure<Tarea>(Exception("Error de prueba"))
        }
        val resultado = MarcarTareaRealizadaUseCase(repo)(tareaId = 1L, realizada = true)
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
