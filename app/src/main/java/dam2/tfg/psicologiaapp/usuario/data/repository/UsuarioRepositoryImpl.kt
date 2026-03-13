package dam2.tfg.psicologiaapp.usuario.data.repository

import dam2.tfg.psicologiaapp.usuario.data.mappers.toDomain
import dam2.tfg.psicologiaapp.usuario.data.mappers.toDto
import dam2.tfg.psicologiaapp.usuario.data.remote.ActualizarEmailRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioApi
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImpl @Inject constructor(
    private val usuarioApi: UsuarioApi
) : UsuarioRepository {

    override suspend fun getPerfilActual(): Result<UsuarioPerfil> = runCatching {
        usuarioApi.getPerfilActual().toDomain()
    }

    override suspend fun crearUsuario(request: UsuarioRequest): Result<Usuario> = runCatching {
        usuarioApi.crearUsuario(request.toDto()).toDomain()
    }

    override suspend fun actualizarEmail(nuevoEmail: String): Result<UsuarioPerfil> = runCatching {
        usuarioApi.actualizarEmail(ActualizarEmailRequestDto(nuevoEmail = nuevoEmail)).toDomain()
    }

    override suspend fun borrarUsuario(): Result<Unit> = runCatching {
        usuarioApi.borrarUsuario()
    }

    override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): Result<Usuario> = runCatching {
        usuarioApi.obtenerUsuarioPorFirebase(fireBaseUid).toDomain()
    }
}
