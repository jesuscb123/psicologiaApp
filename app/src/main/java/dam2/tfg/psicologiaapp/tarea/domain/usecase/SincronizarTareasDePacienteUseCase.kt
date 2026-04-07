package dam2.tfg.psicologiaapp.tarea.domain.usecase

import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject

class SincronizarTareasDePacienteUseCase @Inject constructor(
    private val tareaRepository: TareaRepository
) {
    suspend operator fun invoke(pacienteId: Long): Result<Unit> =
        tareaRepository.sincronizarTareasDePaciente(pacienteId)
}

