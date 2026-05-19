package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import javax.inject.Inject

class SincronizarPacientesDePsicologoUseCase @Inject constructor(
    private val psicologoRepository: PsicologoRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        psicologoRepository.getPacientesDePsicologo().getOrThrow()
        Unit
    }
}
