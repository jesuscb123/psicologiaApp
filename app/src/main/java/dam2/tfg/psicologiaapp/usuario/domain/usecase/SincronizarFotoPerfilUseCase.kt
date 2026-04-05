package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

/**
 * Envía la imagen al backend (multipart); el servidor la guarda y devuelve el perfil actualizado.
 */
class SincronizarFotoPerfilUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository,
) {
    suspend operator fun invoke(bytes: ByteArray, tipoMime: String?): Result<UsuarioPerfil> {
        val mime = tipoMime?.takeIf { it.isNotBlank() } ?: "image/jpeg"
        return usuarioRepository.subirFotoPerfil(bytes, mime)
    }
}
