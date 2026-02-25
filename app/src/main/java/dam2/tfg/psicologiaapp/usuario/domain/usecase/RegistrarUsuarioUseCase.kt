package dam2.tfg.psicologiaapp.usuario.domain.usecase

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class RegistrarUsuarioUseCase @Inject constructor(
    private val repository: UsuarioRepository
) {
    suspend operator fun invoke(
        firebaseUid: String,
        email: String,
        nombreUsuario: String,
        fotoPerfilBase64: String?,
        numeroColegiado: String? = null // <--- Nuevo parámetro opcional
    ): Result<Usuario> {

        // Validaciones de negocio comunes
        if (nombreUsuario.isBlank()) {
            return Result.failure(Exception("El nombre de usuario no puede estar vacío"))
        }

        if (email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }

        return repository.registrarUsuario(
            firebaseUid = firebaseUid,
            email = email,
            nombreUsuario = nombreUsuario,
            fotoPerfilBase64 = fotoPerfilBase64,
            numeroColegiado = numeroColegiado // <--- Se lo pasamos al repo
        )
    }
}