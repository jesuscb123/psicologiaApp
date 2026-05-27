package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.repository.TemaPreferenciasRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

open class FakeTemaPreferenciasRepository(
    private val modo: ModoTemaApp = ModoTemaApp.SeguirSistema,
) : TemaPreferenciasRepository {
    override fun observarModoTema(): Flow<ModoTemaApp> = flowOf(modo)
    override suspend fun establecerModoTema(modo: ModoTemaApp) {}
}
