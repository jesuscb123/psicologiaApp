package dam2.tfg.psicologiaapp.usuario.domain.repository

import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario


interface UsuarioRepository {
    suspend fun registrarUsuario(
        email: String,
        password: String,
        nombreUsuario: String,
        fotoPerfilBase64: String?,
        numeroColegiado: String?,
        especialidad: String?
    ): Result<Usuario>

    suspend fun obtenerUsuarioLocal(): Result<Usuario?>

    // Borra los datos locales al cerrar sesión
    suspend fun cerrarSesion()
}