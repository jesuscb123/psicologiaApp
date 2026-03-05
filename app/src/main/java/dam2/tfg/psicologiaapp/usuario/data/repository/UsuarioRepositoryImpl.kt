package dam2.tfg.psicologiaapp.usuario.data.repository

import com.google.firebase.auth.FirebaseAuth
import dam2.tfg.psicologiaapp.core.utils.auth.AuthManager
import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import dam2.tfg.psicologiaapp.usuario.data.mapper.toDomain
import dam2.tfg.psicologiaapp.usuario.data.mapper.toEntity
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioApi
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val api: UsuarioApi,
    private val dao: UsuarioDao,
    private val auth: AuthManager
) : UsuarioRepository {

    override suspend fun registrarUsuario(
        email: String,
        password: String,
        nombreUsuario: String,
        fotoPerfilBase64: String?,
        numeroColegiado: String?,
        especialidad: String?
    ): Result<Usuario> {
        return try {
            // 1. Firebase
            val uid = auth.crearUsuarioConEmailPassword(email, password)
                ?: return Result.failure(Exception("Error al crear usuario en Firebase"))

            val tokenReal = auth.obtenerIdToken()
                ?: return Result.failure(Exception("No se pudo generar el Token de seguridad"))

            // 2. ¡AQUÍ MONTAS EL REQUEST! (Dentro de la capa de Datos)
            val request = UsuarioRequest(
                nombreUsuario = nombreUsuario,
                fotoPerfilUrl = fotoPerfilBase64,
                numeroColegiado = numeroColegiado,
                especialidad = especialidad,
                rol = if (numeroColegiado != null) "PSICOLOGO" else "PACIENTE"
            )

            val response = api.registrarUsuarioCompleto("Bearer $tokenReal", request)

            if (response.isSuccessful && response.body() != null) {
                val entity = response.body()!!.toEntity()
                dao.guardarUsuario(entity)
                Result.success(entity.toDomain())
            } else {
                auth.eliminarUsuarioActual()
                Result.failure(Exception("Error en servidor: ${response.errorBody()?.string()}"))
            }
        } catch (e: Exception) {
            auth.eliminarUsuarioActual()
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