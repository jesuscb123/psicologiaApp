package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import javax.inject.Inject

class ReservarCitaUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
) {
    suspend operator fun invoke(inicioIsoOffset: String, zonaHoraria: String): Result<Cita> =
        citaRepository.reservarCita(inicioIsoOffset = inicioIsoOffset, zonaHoraria = zonaHoraria)
}

