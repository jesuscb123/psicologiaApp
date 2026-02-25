package dam2.tfg.psicologiaapp.usuario.data.repository

import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import dam2.tfg.psicologiaapp.usuario.data.mapper.toDomain
import dam2.tfg.psicologiaapp.usuario.data.mapper.toEntity
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioApi
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val api: UsuarioApi,
    private val dao: UsuarioDao
) : UsuarioRepository {

    override suspend fun registrarUsuario(
        firebaseUid: String,
        email: String,
        nombreUsuario: String,
        fotoPerfilBase64: String?
    ): Result<Usuario> {
        return try {
            val request = UsuarioRequest(nombreUsuario, fotoPerfilBase64)

            // OJO: Aquí estoy simulando el token, luego veremos cómo inyectarlo bien con Firebase Auth
            val response = api.registrarUsuario("Bearer $firebaseUid", request)

            if (response.isSuccessful && response.body() != null) {
                val usuarioResponse = response.body()!!

                val entity = usuarioResponse.toEntity()
                dao.guardarUsuario(entity)
                Result.success(entity.toDomain())
            } else {
                Result.failure(Exception("Error en servidor: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun obtenerUsuarioLocal(): Result<Usuario?> {
        return try {
            val entity = dao.obtenerUsuarioActual()
            Result.success(entity?.toDomain())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun cerrarSesion() {
        dao.borrarUsuario()
    }
}