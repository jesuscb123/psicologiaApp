package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import javax.inject.Inject

class BuscarPacientesUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(nombreUsuario: String): Result<List<Paciente>> =
        pacienteRepository.buscarPacientes(nombreUsuario)
}
