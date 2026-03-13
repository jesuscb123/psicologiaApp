package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.Paciente
import javax.inject.Inject

class GetPacientesDePsicologoUseCase @Inject constructor(
    private val psicologoRepository: PsicologoRepository
) {
    suspend operator fun invoke(): Result<List<Paciente>> = psicologoRepository.getPacientesDePsicologo()
}
