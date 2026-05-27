package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioSinRol
import dam2.tfg.psicologiaapp.test.fakes.FakeUsuarioRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ObtenerUsuarioPorFirebaseUseCaseTest {

    @Test
    fun `invoke debe propagar exito del repositorio`() = runTest {
        val expected = UsuarioSinRol(1L, "uid", "Nombre", "Apellidos", null)
        val repo = object : FakeUsuarioRepository() {
            override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String) = Result.success(expected)
        }
        val resultado = ObtenerUsuarioPorFirebaseUseCase(repo)(fireBaseUid = "uid")
        assertTrue(resultado.isSuccess)
        assertEquals(expected, resultado.getOrNull())
    }

    @Test
    fun `invoke debe propagar fallo del repositorio`() = runTest {
        val repo = object : FakeUsuarioRepository() {
            override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String) =
                Result.failure<dam2.tfg.psicologiaapp.usuario.domain.model.Usuario>(Exception("Error de prueba"))
        }
        val resultado = ObtenerUsuarioPorFirebaseUseCase(repo)(fireBaseUid = "uid")
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
