package dam2.tfg.psicologiaapp.nota.domain.usecase

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import dam2.tfg.psicologiaapp.nota.domain.repository.NotaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarNotasPacienteActualUseCase @Inject constructor(
    private val notaRepository: NotaRepository
) {
    operator fun invoke(): Flow<List<Nota>> =
        notaRepository.observarNotasPacienteActual()
}

