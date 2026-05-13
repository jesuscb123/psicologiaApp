package dam2.tfg.psicologiaapp.resumenIa.domain.usecase

import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa
import dam2.tfg.psicologiaapp.resumenIa.domain.repository.ResumenIaRepository
import javax.inject.Inject

class GenerarResumenIaPacienteUseCase @Inject constructor(
    private val resumenIaRepository: ResumenIaRepository,
) {
    suspend operator fun invoke(pacienteId: Long): Result<ResumenIa> =
        resumenIaRepository.generarResumenNotasPaciente(pacienteId)
}
