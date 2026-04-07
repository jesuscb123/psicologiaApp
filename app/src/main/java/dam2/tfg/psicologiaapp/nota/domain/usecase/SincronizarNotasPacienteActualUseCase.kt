package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject

class SincronizarNotasPacienteActualUseCase @Inject constructor(
    private val notaRepository: NotaRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        notaRepository.sincronizarNotasPacienteActual()
}

