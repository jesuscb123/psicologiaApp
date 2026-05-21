package dam2.tfg.psicologiaapp.notificaciones.domain.usecase

import dam2.tfg.psicologiaapp.notificaciones.domain.repository.AlertaRiesgoRepository
import javax.inject.Inject

class LimpiarAlertaRiesgoPacienteUseCase @Inject constructor(
    private val alertaRiesgoRepository: AlertaRiesgoRepository,
) {
    suspend operator fun invoke(psicologoUid: String, pacienteId: Long) {
        if (psicologoUid.isBlank() || pacienteId <= 0L) return
        alertaRiesgoRepository.limpiarAlertaRiesgo(psicologoUid, pacienteId)
    }
}
