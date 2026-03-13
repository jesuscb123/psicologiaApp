package dam2.tfg.psicologiaapp.paciente.domain.usecase

import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import dam2.tfg.psicologiaapp.usuario.domain.model.Paciente
import javax.inject.Inject

class GetPacientePorFirebaseUseCase @Inject constructor(
    private val pacienteRepository: PacienteRepository
) {
    suspend operator fun invoke(firebaseId: String): Result<Paciente> =
        pacienteRepository.getPacientePorFirebase(firebaseId)
}
