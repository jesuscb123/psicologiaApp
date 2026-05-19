package dam2.tfg.psicologiaapp.cita.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.model.Cita
import dam2.tfg.psicologiaapp.cita.domain.repository.CitaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarMisCitasPacienteUseCase @Inject constructor(
    private val citaRepository: CitaRepository,
) {
    operator fun invoke(): Flow<List<Cita>> =
        citaRepository.observarMisCitasPaciente()
}
