package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import javax.inject.Inject

class ObtenerPerfilCacheadoUseCase @Inject constructor(
    private val usuarioCacheRepository: UsuarioCacheRepository,
) {
    suspend operator fun invoke(firebaseUid: String): PerfilCacheado? =
        usuarioCacheRepository.obtenerPerfilCacheadoPorFirebaseUid(firebaseUid)
}

