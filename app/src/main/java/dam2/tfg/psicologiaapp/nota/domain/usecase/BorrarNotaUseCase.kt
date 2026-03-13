package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject

class BorrarNotaUseCase @Inject constructor(
    private val notaRepository: NotaRepository
) {
    suspend operator fun invoke(notaId: Long): Result<Unit> = notaRepository.borrarNota(notaId)
}
