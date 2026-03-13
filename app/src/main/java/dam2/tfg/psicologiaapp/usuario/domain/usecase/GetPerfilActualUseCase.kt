package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class GetPerfilActualUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(): Result<UsuarioPerfil> = usuarioRepository.getPerfilActual()
}
