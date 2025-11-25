package dam2.tfg.psicologiaapp.data.repository

import androidx.annotation.OptIn
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import dam2.tfg.psicologiaapp.data.local.dao.UsuarioDao
import dam2.tfg.psicologiaapp.data.remote.api.PsicologiaApi
import dam2.tfg.psicologiaapp.data.remote.dto.CrearUsuarioRequest
import dam2.tfg.psicologiaapp.data.toDomain
import dam2.tfg.psicologiaapp.data.toEntity
import dam2.tfg.psicologiaapp.domain.model.Usuario
import dam2.tfg.psicologiaapp.domain.repository.UsuarioRepository
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val api: PsicologiaApi,
    private val usuarioDao: UsuarioDao
) : UsuarioRepository {

    override suspend fun getUsuarioByFirebaseUid(firebaseUid: String): Usuario {
        val dto = api.getUsuarioByFirebaseUid(firebaseUid)
        val usuario = dto.toDomain()
        usuarioDao.insertUsuario(usuario.toEntity())
        return usuario
    }

    override suspend fun getUsuariosRegistrados(): List<Usuario> {
        val dtos = api.getUsuariosRegistrados()
        val usuarios = dtos.map { it.toDomain() }
        usuarioDao.insertUsuarios(usuarios.map { it.toEntity() })
        return usuarios
    }

    @OptIn(UnstableApi::class)
    override suspend fun registrarUsuarioEnBackend(
        idToken: String,
        nombreUsuario: String
    ): Usuario {
        return try {
            Log.d("UsuarioRepo", "Llamando a crearUsuario en backend, nombre=$nombreUsuario")

            val request = CrearUsuarioRequest(nombreUsuario = nombreUsuario)

            val dto = api.crearUsuario(
                authHeader = "Bearer $idToken",
                request = request
            )

            Log.d("UsuarioRepo", "Backend respondió, firebaseUid=${dto.firebaseUid}, email=${dto.email}")

            val usuario = dto.toDomain()
            usuarioDao.insertUsuario(usuario.toEntity())
            usuario
        } catch (e: Exception) {
            Log.e("UsuarioRepo", "Error al registrar usuario en backend", e)
            throw e
        }
    }
}
