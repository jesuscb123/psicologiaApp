package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import javax.inject.Inject

class BuscarPsicologosUseCase @Inject constructor(
    private val psicologoRepository: PsicologoRepository
) {
    suspend operator fun invoke(nombreUsuario: String): Result<List<Psicologo>> =
        psicologoRepository.buscarPsicologos(nombreUsuario)
}
