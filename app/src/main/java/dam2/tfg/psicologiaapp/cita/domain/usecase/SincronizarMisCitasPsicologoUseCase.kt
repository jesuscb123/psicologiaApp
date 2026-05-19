package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import javax.inject.Inject

class SincronizarMisCitasPsicologoUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
) {
    suspend operator fun invoke(): Result<Unit> =
        citaRepository.sincronizarMisCitasPsicologo()
}
