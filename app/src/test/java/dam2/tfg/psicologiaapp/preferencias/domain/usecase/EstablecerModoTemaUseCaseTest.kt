package dam2.tfg.psicologiaapp.preferencias.domain.usecase

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.test.fakes.FakeTemaPreferenciasRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EstablecerModoTemaUseCaseTest {

    @Test
    fun `invoke debe establecer modo tema correctamente`() = runTest {
        var modoEstablecido: ModoTemaApp? = null
        val repo = object : FakeTemaPreferenciasRepository() {
            override suspend fun establecerModoTema(modo: ModoTemaApp) {
                modoEstablecido = modo
            }
        }
        val resultado = EstablecerModoTemaUseCase(repo)(ModoTemaApp.Oscuro)
        assertTrue(resultado.isSuccess)
        assertEquals(ModoTemaApp.Oscuro, modoEstablecido)
    }

    @Test
    fun `invoke debe devolver fallo si el repositorio lanza excepcion`() = runTest {
        val repo = object : FakeTemaPreferenciasRepository() {
            override suspend fun establecerModoTema(modo: ModoTemaApp) {
                throw Exception("Error de prueba")
            }
        }
        val resultado = EstablecerModoTemaUseCase(repo)(ModoTemaApp.Claro)
        assertTrue(resultado.isFailure)
        assertEquals("Error de prueba", resultado.exceptionOrNull()?.message)
    }
}
