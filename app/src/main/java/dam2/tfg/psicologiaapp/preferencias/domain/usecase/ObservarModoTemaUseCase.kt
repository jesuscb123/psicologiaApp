package dam2.tfg.psicologiaapp.preferencias.domain.usecase

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.repository.TemaPreferenciasRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarModoTemaUseCase @Inject constructor(
    private val temaPreferenciasRepository: TemaPreferenciasRepository,
) {
    operator fun invoke(): Flow<ModoTemaApp> = temaPreferenciasRepository.observarModoTema()
}
