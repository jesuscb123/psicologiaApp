package dam2.tfg.psicologiaapp.psicologo.data.remote

import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioPerfilResponseDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioRequestDto
import dam2.tfg.psicologiaapp.usuario.data.remote.UsuarioResponseDto

data class PsicologoRequestDto(
    override val nombre: String,
    override val apellidos: String,
    override val fotoPerfilUrl: String? = null,
    val numeroColegiado: String,
    val especialidades: List<String>,
    val descripcion: String? = null,
    override val rol: String = "PSICOLOGO"
) : UsuarioRequestDto

data class PsicologoResponseDto(
    override val id: Long,
    /** ID de la entidad Psicólogo (tabla PSICOLOGOS); [id] es el ID de usuario. */
    val idEntidadPsicologo: Long,
    override val firebaseUid: String,
    override val nombre: String,
    override val apellidos: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val numeroColegiado: String,
    val especialidades: List<String>,
    val descripcion: String? = null,
) : UsuarioResponseDto

data class PsicologoPerfilResponseDto(
    override val id: Long,
    override val firebaseUid: String,
    override val nombre: String,
    override val apellidos: String,
    override val email: String,
    override val fotoPerfilUrl: String?,
    override val rol: String,
    val numeroColegiado: String,
    val especialidades: List<String>,
    val descripcion: String? = null,
) : UsuarioPerfilResponseDto
