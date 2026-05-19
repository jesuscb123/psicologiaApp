package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class ObservarPerfilCacheadoUseCase @Inject constructor(
    private val usuarioCacheRepository: UsuarioCacheRepository,
) {
    operator fun invoke(): Flow<PerfilCacheado?> =
        usuarioCacheRepository.observarPerfilCacheado()
}
