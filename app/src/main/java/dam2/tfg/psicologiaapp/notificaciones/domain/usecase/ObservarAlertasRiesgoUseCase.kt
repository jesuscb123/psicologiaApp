package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.AlertaRiesgoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservarAlertasRiesgoUseCase @Inject constructor(
    private val alertaRiesgoRepository: AlertaRiesgoRepository,
) {
    operator fun invoke(psicologoUid: String): Flow<Set<Long>> =
        alertaRiesgoRepository.observarAlertasRiesgo(psicologoUid)
}
