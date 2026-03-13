package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject

class MarcarTareaRealizadaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(tareaId: Long, realizada: Boolean): Result<Tarea> =
        tareaRepository.marcarRealizada(tareaId, realizada)
}
