package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.test.fakes.FakePsicologoRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetPsicologoPorFirebaseUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = Psicologo(1L, 10L, "uid", "Dr", "House", null, "12345", listOf("Clinica"), "Desc")
        val repo = object : FakePsicologoRepository() {
            override suspend fun getPsicologoPorFirebase(firebaseId: String) = Result.success(expected)
        }
        val resultado = GetPsicologoPorFirebaseUseCase(repo)(firebaseId = "uid")
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakePsicologoRepository() {
            override suspend fun getPsicologoPorFirebase(firebaseId: String) =
                Result.failure<Psicologo>(Exception("Error de prueba"))
        }
        val resultado = GetPsicologoPorFirebaseUseCase(repo)(firebaseId = "uid")
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
