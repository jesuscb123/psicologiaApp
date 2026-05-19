package dam2.tfg.psicologiaapp.psicologo.domain.usecase

import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarPsicologosUseCase @Inject constructor(
    private val psicologoRepository: PsicologoRepository,
) {
    operator fun invoke(): Flow<List<Psicologo>> =
        psicologoRepository.observarPsicologos()
}
