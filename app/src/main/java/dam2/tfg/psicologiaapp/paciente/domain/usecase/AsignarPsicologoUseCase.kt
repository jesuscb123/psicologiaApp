package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import javax.inject.Inject

class AsignarPsicologoUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(psicologoId: Long): Result<Paciente> =
        pacienteRepository.asignarPsicologo(psicologoId)
}
