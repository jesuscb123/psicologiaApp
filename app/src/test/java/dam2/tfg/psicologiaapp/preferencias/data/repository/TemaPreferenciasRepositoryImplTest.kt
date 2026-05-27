package dam2.tfg.psicologiaapp.preferencias.data.repository

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.test.FakeDataStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TemaPreferenciasRepositoryImplTest {

    private lateinit var dataStore: FakeDataStore
    private lateinit var repository: TemaPreferenciasRepositoryImpl

    @Before
    fun setUp() {
        dataStore = FakeDataStore()
        repository = TemaPreferenciasRepositoryImpl(dataStore)
    }

    @Test
    fun `observarModoTema debe emitir SeguirSistema por defecto`() = runTest {
        repository.observarModoTema().test {
            assertEquals(ModoTemaApp.SeguirSistema, awaitItem())
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `establecerModoTema debe persistir y emitir el modo elegido`() = runTest {
        repository.observarModoTema().test {
            assertEquals(ModoTemaApp.SeguirSistema, awaitItem())

            repository.establecerModoTema(ModoTemaApp.Oscuro)
            assertEquals(ModoTemaApp.Oscuro, awaitItem())

            repository.establecerModoTema(ModoTemaApp.Claro)
            assertEquals(ModoTemaApp.Claro, awaitItem())

            cancelAndIgnoreRemainingEvents()
        }
    }
}
