package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject

class GetNotasDePacienteUseCase @Inject constructor(
    private val notaRepository: NotaRepository
) {
    suspend operator fun invoke(pacienteId: Long): Result<List<Nota>> =
        notaRepository.getNotasDePaciente(pacienteId)
}
