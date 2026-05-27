package dam2.tfg.psicologiaapp.usuario.domain.usecase

import androidx.datastore.preferences.core.stringPreferencesKey
import dam2.tfg.psicologiaapp.data.local.PsicologiaAppDatabase
import dam2.tfg.psicologiaapp.test.FakeDataStore
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class LimpiarTodosDatosLocalesUseCaseTest {

    @Test
    fun `invoke debe limpiar base de datos y datastore`() = runTest {
        val baseDeDatos = mock(PsicologiaAppDatabase::class.java)
        val clave = stringPreferencesKey("clave_prueba")
        val dataStore = FakeDataStore()
        dataStore.updateData { prefs -> prefs.toMutablePreferences().apply { this[clave] = "valor" } }

        LimpiarTodosDatosLocalesUseCase(baseDeDatos, dataStore)()

        verify(baseDeDatos).clearAllTables()
        assertTrue(dataStore.snapshot().asMap().isEmpty())
    }
}
