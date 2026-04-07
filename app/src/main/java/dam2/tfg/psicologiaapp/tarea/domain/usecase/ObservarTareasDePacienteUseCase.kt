package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarTareasDePacienteUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    operator fun invoke(pacienteId: Long): Flow<List<Tarea>> =
        tareaRepository.observarTareasDePaciente(pacienteId)
}

