package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarNotasDePacienteUseCase @Inject constructor(
    private val notaRepository: NotaRepository
) {
    operator fun invoke(pacienteId: Long): Flow<List<Nota>> =
        notaRepository.observarNotasDePaciente(pacienteId)
}

