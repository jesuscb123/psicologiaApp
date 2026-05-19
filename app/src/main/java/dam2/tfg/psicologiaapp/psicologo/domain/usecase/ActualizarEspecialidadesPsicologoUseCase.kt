package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import javax.inject.Inject

class ActualizarEspecialidadesPsicologoUseCase @Inject constructor(
    private val psicologoRepository: PsicologoRepository
) {
    suspend operator fun invoke(especialidades: List<String>): Result<Psicologo> =
        psicologoRepository.actualizarMisEspecialidades(especialidades)
}
