package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EliminarTareaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Unit
        val repo = object : FakeTareaRepository() {
            override suspend fun eliminarTarea(tareaId: Long) = Result.success(expected)
        }
        val resultado = EliminarTareaUseCase(repo)(tareaId = 1L)
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeTareaRepository() {
            override suspend fun eliminarTarea(tareaId: Long) =
                Result.failure<Unit>(Exception("Error de prueba"))
        }
        val resultado = EliminarTareaUseCase(repo)(tareaId = 1L)
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
