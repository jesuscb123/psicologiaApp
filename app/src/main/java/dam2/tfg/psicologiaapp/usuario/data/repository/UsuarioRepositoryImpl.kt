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
        fotoPerfilBase64: String?,
        numeroColegiado: String?
    ): Result<Usuario> {
        return try {
            // 1. Creamos el Request para la API (incluye el número de colegiado)
            val request = UsuarioRequest(
                nombreUsuario = nombreUsuario,
                fotoPerfilUrl = fotoPerfilBase64,
                numeroColegiado = numeroColegiado
            )

            // 2. Llamada a Retrofit
            val response = api.registrarUsuario("Bearer $firebaseUid", request)

            if (response.isSuccessful && response.body() != null) {
                val usuarioResponse = response.body()!!

                // 3. Convertimos a Entity (Room) y guardamos
                // El mapper ahora se encarga de que la Entity tenga el numeroColegiado
                val entity = usuarioResponse.toEntity()
                dao.guardarUsuario(entity)

                // 4. Devolvemos el modelo de dominio (que será Paciente o Psicologo)
                Result.success(entity.toDomain())
            } else {
                Result.failure(Exception("Error en el servidor: ${response.code()}"))
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