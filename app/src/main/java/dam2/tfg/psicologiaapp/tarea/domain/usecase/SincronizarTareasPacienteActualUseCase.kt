package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject

class SincronizarTareasPacienteActualUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        tareaRepository.sincronizarTareasPacienteActual()
}

