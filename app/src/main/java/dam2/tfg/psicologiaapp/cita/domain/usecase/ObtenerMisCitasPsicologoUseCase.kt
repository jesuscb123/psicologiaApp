package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import javax.inject.Inject

class ObtenerMisCitasPsicologoUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
) {
    suspend operator fun invoke(): Result<List<Cita>> = citaRepository.getMisCitasPsicologo()
}

