package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class RegistrarUsuarioEnBackendUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(
        idToken: String,
        nombreUsuario: String
    ): Usuario {
        return usuarioRepository.registrarUsuarioEnBackend(idToken, nombreUsuario)
    }
}