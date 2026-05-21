package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.AlertaRiesgoRepository
import javax.inject.Inject

class MarcarAlertaRiesgoUseCase @Inject constructor(
    private val alertaRiesgoRepository: AlertaRiesgoRepository,
) {
    suspend operator fun invoke(psicologoUid: String, pacienteId: Long) {
        if (psicologoUid.isBlank() || pacienteId <= 0L) return
        alertaRiesgoRepository.marcarAlertaRiesgo(psicologoUid, pacienteId)
    }
}
