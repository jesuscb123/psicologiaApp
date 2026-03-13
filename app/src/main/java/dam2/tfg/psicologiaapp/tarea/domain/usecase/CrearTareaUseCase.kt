package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject

class CrearTareaUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(
        pacienteId: Long,
        titulo: String,
        descripcion: String
    ): Result<Tarea> = tareaRepository.crearTarea(pacienteId, titulo, descripcion)
}
