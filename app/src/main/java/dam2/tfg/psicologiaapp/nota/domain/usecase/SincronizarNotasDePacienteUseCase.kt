package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject

class SincronizarNotasDePacienteUseCase @Inject constructor(
    private val notaRepository: NotaRepository
) {
    suspend operator fun invoke(pacienteId: Long): Result<Unit> =
        notaRepository.sincronizarNotasDePaciente(pacienteId)
}

