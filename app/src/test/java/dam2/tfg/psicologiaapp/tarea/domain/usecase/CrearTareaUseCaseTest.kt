package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.test.fakes.FakeTareaRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CrearTareaUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Tarea(1L, "Titulo", "Desc", "2024-01-01", false, true, 2L, 1L)
        val repo = object : FakeTareaRepository() {
            override suspend fun crearTarea(pacienteId: Long, titulo: String, descripcion: String) = Result.success(expected)
        }
        val resultado = CrearTareaUseCase(repo)(pacienteId = 1L, titulo = "T", descripcion = "D")
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeTareaRepository() {
            override suspend fun crearTarea(pacienteId: Long, titulo: String, descripcion: String) =
                Result.failure<Tarea>(Exception("Error de prueba"))
        }
        val resultado = CrearTareaUseCase(repo)(pacienteId = 1L, titulo = "T", descripcion = "D")
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
