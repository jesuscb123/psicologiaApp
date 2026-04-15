package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.model.DisponibilidadDia
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import java.time.LocalDate
import javax.inject.Inject

class ObtenerDisponibilidadDiaUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
) {
    suspend operator fun invoke(fecha: LocalDate, zonaHoraria: String): Result<DisponibilidadDia> =
        citaRepository.getDisponibilidadDia(fecha, zonaHoraria)
}

