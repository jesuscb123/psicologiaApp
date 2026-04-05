package dam2.tfg.psicologiaapp.usuario.domain.repository

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest

/**
 * Contrato del repositorio de usuarios en dominio.
 * La implementación en data usará la API remota y mappers.
 */
interface UsuarioRepository {

    suspend fun getPerfilActual(): Result<UsuarioPerfil>

    suspend fun crearUsuario(request: UsuarioRequest): Result<Usuario>

    suspend fun actualizarEmail(nuevoEmail: String): Result<UsuarioPerfil>

    suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<UsuarioPerfil>

    suspend fun borrarUsuario(): Result<Unit>

    suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): Result<Usuario>
}
