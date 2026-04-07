package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import javax.inject.Inject

class LimpiarPerfilCacheadoUseCase @Inject constructor(
    private val usuarioCacheRepository: UsuarioCacheRepository,
) {
    suspend operator fun invoke() {
        usuarioCacheRepository.limpiarCache()
    }
}

