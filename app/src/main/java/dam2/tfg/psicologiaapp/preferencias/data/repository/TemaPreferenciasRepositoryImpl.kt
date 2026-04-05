package dam2.tfg.psicologiaapp.preferencias.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.repository.TemaPreferenciasRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val claveModoTema = stringPreferencesKey("modo_tema")

private const val valorSistema = "sistema"
private const val valorClaro = "claro"
private const val valorOscuro = "oscuro"

private fun ModoTemaApp.aCadenaAlmacenada(): String = when (this) {
    ModoTemaApp.SeguirSistema -> valorSistema
    ModoTemaApp.Claro -> valorClaro
    ModoTemaApp.Oscuro -> valorOscuro
}

private fun String?.aModoTemaApp(): ModoTemaApp = when (this) {
    valorClaro -> ModoTemaApp.Claro
    valorOscuro -> ModoTemaApp.Oscuro
    else -> ModoTemaApp.SeguirSistema
}

class TemaPreferenciasRepositoryImpl @Inject constructor(
    private val preferenciasDataStore: DataStore<Preferences>,
) : TemaPreferenciasRepository {

    override fun observarModoTema(): Flow<ModoTemaApp> =
        preferenciasDataStore.data.map { preferencias ->
            preferencias[claveModoTema].aModoTemaApp()
        }

    override suspend fun establecerModoTema(modo: ModoTemaApp) {
        preferenciasDataStore.edit { preferencias ->
            preferencias[claveModoTema] = modo.aCadenaAlmacenada()
        }
    }
}
