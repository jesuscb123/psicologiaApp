package dam2.tfg.psicologiaapp.usuario.data.repository

import dam2.tfg.psicologiaapp.BuildConfig
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.usuario.data.mappers.toDomain
import dam2.tfg.psicologiaapp.usuario.data.mappers.toDto
import dam2.tfg.psicologiaapp.usuario.data.remote.ActualizarEmailRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioApi
import dam2.tfg.psicologiaapp.usuario.domain.model.Usuario
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfilBasico
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioRequest
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioRepository
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioRepositoryImpl @Inject constructor(
    private val usuarioApi: UsuarioApi
) : UsuarioRepository {

    override suspend fun existeCorreo(email: String): Result<Boolean> = runCatching {
        usuarioApi.existeCorreo(email = email).existe
    }

    override suspend fun getPerfilActual(): Result<UsuarioPerfil> = runCatching {
        usuarioApi.getPerfilActual().toDomain().conFotoUrlNormalizada()
    }

    override suspend fun crearUsuario(request: UsuarioRequest): Result<Usuario> = runCatching {
        usuarioApi.crearUsuario(request.toDto()).toDomain()
    }

    override suspend fun actualizarEmail(nuevoEmail: String): Result<UsuarioPerfil> = runCatching {
        usuarioApi.actualizarEmail(ActualizarEmailRequestDto(nuevoEmail = nuevoEmail))
            .toDomain()
            .conFotoUrlNormalizada()
    }

    override suspend fun subirFotoPerfil(bytes: ByteArray, tipoMime: String): Result<UsuarioPerfil> = runCatching {
        val mediaType = tipoMime.toMediaTypeOrNull()
            ?: "application/octet-stream".toMediaTypeOrNull()
        val cuerpo = bytes.toRequestBody(mediaType)
        val extension = when {
            tipoMime.contains("png", ignoreCase = true) -> "png"
            tipoMime.contains("webp", ignoreCase = true) -> "webp"
            else -> "jpg"
        }
        val parte = MultipartBody.Part.createFormData("archivo", "foto_perfil.$extension", cuerpo)
        usuarioApi.subirFotoPerfil(parte).toDomain().conFotoUrlNormalizada()
    }

    /**
     * Si el API devolvió una URL con localhost (p. ej. servidor sin APP_URL_PUBLICA_BASE),
     * sustituye el origen por [BuildConfig.BASE_URL] para que Coil y el dispositivo resuelvan el host correcto.
     */
    private fun normalizarUrlFotoParaCliente(url: String?): String? {
        val u = url?.trim()?.takeIf { it.isNotEmpty() } ?: return null
        val lower = u.lowercase()
        val apuntaABucleLocal = lower.contains("localhost") || lower.contains("127.0.0.1")
        if (!apuntaABucleLocal) return u
        val idx = u.indexOf("/api/")
        if (idx < 0) return u
        val base = BuildConfig.BASE_URL.trimEnd('/')
        return base + u.substring(idx)
    }

    private fun UsuarioPerfil.conFotoUrlNormalizada(): UsuarioPerfil {
        val nu = normalizarUrlFotoParaCliente(fotoPerfilUrl)
        return when (this) {
            is PacientePerfil -> copy(fotoPerfilUrl = nu)
            is PsicologoPerfil -> copy(fotoPerfilUrl = nu)
            is UsuarioPerfilBasico -> copy(fotoPerfilUrl = nu)
            else -> this
        }
    }

    override suspend fun borrarUsuario(): Result<Unit> = runCatching {
        usuarioApi.borrarUsuario()
    }

    override suspend fun obtenerUsuarioPorFirebase(fireBaseUid: String): Result<Usuario> = runCatching {
        usuarioApi.obtenerUsuarioPorFirebase(fireBaseUid).toDomain()
    }
}
