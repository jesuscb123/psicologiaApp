package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class SincronizarPsicologosUseCaseTest {

    @Test
    fun `invoke debe completar cuando el repositorio tiene exito`() = runTest {
        val repo = object : FakePsicologoRepository() {
            override suspend fun listarPsicologos() = Result.success(listOf(Psicologo(1L, 10L, "uid", "Dr", "House", null, "12345", listOf("Clinica"), "Desc")))
        }
        val resultado = SincronizarPsicologosUseCase(repo)()
        assertTrue(resultado.isSuccess)
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakePsicologoRepository() {
            override suspend fun listarPsicologos() =
                Result.failure<List<Psicologo>>(Exception("Error de prueba"))
        }
        val resultado = SincronizarPsicologosUseCase(repo)()
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
