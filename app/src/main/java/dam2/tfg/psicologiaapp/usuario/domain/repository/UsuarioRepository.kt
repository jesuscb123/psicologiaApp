package dam2.tfg.psicologiaapp.usuario.domain.repository

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario

interface UsuarioRepository {

    suspend fun getUsuarioByFirebaseUid(firebaseUid: String): Usuario
    suspend fun getUsuariosRegistrados(): List<Usuario>

    suspend fun registrarUsuarioEnBackend(
        idToken: String,
        nombreUsuario: String
    ): Usuario
}