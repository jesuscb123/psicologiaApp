package dam2.tfg.psicologiaapp.psicologo.domain.model

data class Psicologo(
    val usuarioId: Long,
    /** ID de la entidad Psicólogo; [usuarioId] es el ID de usuario. */
    val idEntidadPsicologo: Long,
    val firebaseUid: String,
    val nombreUsuario: String,
    val fotoPerfilUrl: String?,
    val numeroColegiado: String,
    val especialidad: String
)

