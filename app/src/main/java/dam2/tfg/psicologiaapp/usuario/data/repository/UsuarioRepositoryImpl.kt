package dam2.tfg.psicologiaapp.usuario.data.repository

import com.google.firebase.auth.FirebaseAuth
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
    private val auth: FirebaseAuth
) : UsuarioRepository {

    override suspend fun registrarUsuario(
        email: String,
        password: String,
        nombreUsuario: String,
        fotoPerfilBase64: String?,
        numeroColegiado: String?
    ): Result<Usuario> {
        return try {
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUid = authResult.user?.uid ?: throw Exception("No se pudo obtener el UID")

            val request = UsuarioRequest(
                nombreUsuario = nombreUsuario,
                fotoPerfilUrl = fotoPerfilBase64,
                numeroColegiado = numeroColegiado
            )

            val response = if (numeroColegiado != null) {
                api.registrarPsicologo("Bearer $firebaseUid", request)
            } else {
                api.registrarPaciente("Bearer $firebaseUid", request)
            }

            if (response.isSuccessful && response.body() != null) {
                val entity = response.body()!!.toEntity()
                dao.guardarUsuario(entity)
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