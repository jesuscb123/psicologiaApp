package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.cita.domain.usecase.SincronizarMisCitasPacienteUseCase
import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import dam2.tfg.psicologiaapp.tarea.domain.usecase.SincronizarTareasPacienteActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import javax.inject.Inject

class CancelarTerapiaUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository,
    private val sincronizarPerfilActualUseCase: SincronizarPerfilActualUseCase,
    private val sincronizarTareasPacienteActualUseCase: SincronizarTareasPacienteActualUseCase,
    private val sincronizarMisCitasPacienteUseCase: SincronizarMisCitasPacienteUseCase,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        pacienteRepository.cancelarTerapia().getOrThrow()
        sincronizarPerfilActualUseCase().getOrThrow()
        sincronizarTareasPacienteActualUseCase().getOrThrow()
        sincronizarMisCitasPacienteUseCase().getOrThrow()
    }
}
