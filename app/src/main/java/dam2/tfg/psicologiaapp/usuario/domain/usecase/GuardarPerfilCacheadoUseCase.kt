package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import javax.inject.Inject

class GuardarPerfilCacheadoUseCase @Inject constructor(
    private val usuarioCacheRepository: UsuarioCacheRepository,
) {
    suspend operator fun invoke(perfil: UsuarioPerfil) {
        usuarioCacheRepository.guardarDesdePerfil(perfil)
    }
}

