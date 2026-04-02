package dam2.tfg.psicologiaapp.auth.domain.usecase

import dam2.tfg.psicologiaapp.auth.domain.repository.AuthRepository
import javax.inject.Inject

class IniciarSesionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(correo: String, contrasena: String): Result<String> =
        authRepository.iniciarSesion(correo = correo, contrasena = contrasena)
}

