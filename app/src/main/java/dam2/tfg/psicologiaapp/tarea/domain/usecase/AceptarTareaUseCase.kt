package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject

class AceptarTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(tareaId: Long): Result<Tarea> = tareaRepository.aceptarTarea(tareaId)
}
