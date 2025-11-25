package dam2.tfg.psicologiaapp.domain.usecase

import dam2.tfg.psicologiaapp.domain.model.Usuario
import dam2.tfg.psicologiaapp.domain.repository.UsuarioRepository
import javax.inject.Inject

class GetUsuarioByFirebaseUidUsedCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(firebaseUid: String): Usuario {
        return usuarioRepository.getUsuarioByFirebaseUid(firebaseUid)

    }
}