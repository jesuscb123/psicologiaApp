package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository

open class FakeUsuarioRepository : UsuarioRepository {
    override suspend fun existeCorreo(email: String): Result<Boolean> = Result.success(false)
    override suspend fun getPerfilActual(): Result<UsuarioPerfil> = Result.failure(NotImplementedError())
    override suspend fun crearUsuario(request: UsuarioRequest): Result<Usuario> = Result.failure(NotImplementedError())
    override suspend fun actualizarEmail(nuevoEmail: String): Result<UsuarioPerfil> = Result.failure(NotImplementedError())
    override suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<UsuarioPerfil> =
        Result.failure(NotImplementedError())
    override suspend fun borrarUsuario(): Result<Unit> = Result.success(Unit)
    override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): Result<Usuario> =
        Result.failure(NotImplementedError())
}
