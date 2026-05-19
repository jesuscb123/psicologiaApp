package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarPacientesDePsicologoUseCase @Inject constructor(
    private val psicologoRepository: PsicologoRepository,
) {
    operator fun invoke(): Flow<List<Paciente>> =
        psicologoRepository.observarPacientesDePsicologo()
}
