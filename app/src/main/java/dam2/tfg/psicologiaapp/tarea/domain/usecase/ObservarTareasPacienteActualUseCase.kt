package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarTareasPacienteActualUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(): Flow<List<Tarea>> =
        tareaRepository.observarTareasPacienteActual()
}

