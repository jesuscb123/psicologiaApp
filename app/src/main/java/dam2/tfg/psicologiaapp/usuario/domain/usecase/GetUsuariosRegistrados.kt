package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class GetUsuariosRegistrados @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(): List<Usuario> {
        return usuarioRepository.getUsuariosRegistrados()
    }
}