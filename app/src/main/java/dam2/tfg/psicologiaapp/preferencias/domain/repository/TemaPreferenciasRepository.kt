package dam2.tfg.psicologiaapp.preferencias.domain.repository

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import kotlinx.coroutines.flow.Flow

interface TemaPreferenciasRepository {

    fun observarModoTema(): Flow<ModoTemaApp>

    suspend fun establecerModoTema(modo: ModoTemaApp)
}
