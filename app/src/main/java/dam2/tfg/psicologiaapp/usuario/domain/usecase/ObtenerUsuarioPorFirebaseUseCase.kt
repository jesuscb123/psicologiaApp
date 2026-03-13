package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class ObtenerUsuarioPorFirebaseUseCase @Inject constructor(
    private val usuarioRepository: UsuarioRepository
) {
    suspend operator fun invoke(fireBaseUid: String): Result<Usuario> =
        usuarioRepository.obtenerUsuarioPorFirebase(fireBaseUid)
}
