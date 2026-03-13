package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class CrearUsuarioUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(request: UsuarioRequest): Result<Usuario> =
        usuarioRepository.crearUsuario(request)
}
