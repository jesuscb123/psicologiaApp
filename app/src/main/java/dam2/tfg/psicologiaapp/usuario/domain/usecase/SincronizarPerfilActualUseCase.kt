package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class SincronizarPerfilActualUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
    private val usuarioCacheRepository: UsuarioCacheRepository,
) {
    suspend operator fun invoke(): Result<Unit> = runCatching {
        val perfil = usuarioRepository.getPerfilActual().getOrThrow()
        usuarioCacheRepository.guardarDesdePerfil(perfil)
    }
}
