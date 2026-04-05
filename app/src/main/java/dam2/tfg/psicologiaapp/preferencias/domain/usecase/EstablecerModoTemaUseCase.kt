package dam2.tfg.psicologiaapp.preferencias.domain.usecase

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.repository.TemaPreferenciasRepository
import javax.inject.Inject

class EstablecerModoTemaUseCase @Inject constructor(
    private val temaPreferenciasRepository: TemaPreferenciasRepository,
) {
    suspend operator fun invoke(modo: ModoTemaApp): Result<Unit> = runCatching {
        temaPreferenciasRepository.establecerModoTema(modo)
    }
}
