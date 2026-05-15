package dam2.tfg.psicologiaapp.usuario.domain.usecase

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import dam2.tfg.psicologiaapp.data.local.PsicologiaAppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Limpia **todos** los datos locales del usuario al cerrar sesión.
 *
 * Borra en orden:
 *  1. Todas las tablas Room (notas, tareas, pacientes, psicólogos, usuarios cacheados).
 *  2. El DataStore de preferencias de sincronización.
 *
 * Motivación de seguridad: en un dispositivo compartido o robado, sin esta limpieza
 * un segundo usuario podría acceder al historial clínico almacenado en la BD local.
 */
class LimpiarTodosDatosLocalesUseCase @Inject constructor(
    private val baseDeDatos: PsicologiaAppDatabase,
    private val preferenciasDataStore: DataStore<Preferences>,
) {
    suspend operator fun invoke() {
        withContext(Dispatchers.IO) {
            baseDeDatos.clearAllTables()
        }
        preferenciasDataStore.edit { it.clear() }
    }
}
