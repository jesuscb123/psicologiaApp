package dam2.tfg.psicologiaapp.auth.domain.usecase

import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class SolicitarRestablecerContrasenaUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(correo: String): Result<Unit> =
        authRepository.solicitarRestablecerContrasena(correo = correo)
}
