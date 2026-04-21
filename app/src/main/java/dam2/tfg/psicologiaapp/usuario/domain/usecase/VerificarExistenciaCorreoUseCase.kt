package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class VerificarExistenciaCorreoUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(email: String): Result<Boolean> =
        usuarioRepository.existeCorreo(email = email)
}
