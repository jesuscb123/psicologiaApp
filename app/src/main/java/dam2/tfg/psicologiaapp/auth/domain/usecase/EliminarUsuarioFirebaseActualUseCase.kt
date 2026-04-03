package dam2.tfg.psicologiaapp.auth.domain.usecase

import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class EliminarUsuarioFirebaseActualUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> =
        authRepository.eliminarUsuarioActual()
}
