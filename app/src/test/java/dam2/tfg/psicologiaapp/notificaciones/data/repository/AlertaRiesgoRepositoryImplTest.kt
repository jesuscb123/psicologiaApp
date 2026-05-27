package dam2.tfg.psicologiaapp.notificaciones.data.repository

import app.cash.turbine.test
import dam2.tfg.psicologiaapp.notificaciones.data.remote.AlertaRiesgoFuenteDatosFirebase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class AlertaRiesgoRepositoryImplTest {

    private val fuenteDatos = mock<AlertaRiesgoFuenteDatosFirebase>()
    private lateinit var repository: AlertaRiesgoRepositoryImpl

    @Before
    fun setUp() {
        repository = AlertaRiesgoRepositoryImpl(fuenteDatos)
    }

    @Test
    fun `observarAlertasRiesgo debe delegar en la fuente Firebase`() = runTest {
        whenever(fuenteDatos.observarAlertasRiesgo("uid-psi")).thenReturn(flowOf(setOf(1L, 2L)))

        repository.observarAlertasRiesgo("uid-psi").test {
            assertEquals(setOf(1L, 2L), awaitItem())
            awaitComplete()
        }
    }

    @Test
    fun `marcarAlertaRiesgo debe delegar en la fuente Firebase`() = runTest {
        repository.marcarAlertaRiesgo("uid-psi", 42L)

        verify(fuenteDatos).marcarAlertaRiesgo("uid-psi", 42L)
    }

    @Test
    fun `limpiarAlertaRiesgo debe delegar en la fuente Firebase`() = runTest {
        repository.limpiarAlertaRiesgo("uid-psi", 42L)

        verify(fuenteDatos).limpiarAlertaRiesgo("uid-psi", 42L)
    }
}
