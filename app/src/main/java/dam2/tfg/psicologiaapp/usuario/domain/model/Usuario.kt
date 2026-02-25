package dam2.tfg.psicologiaapp.usuario.domain.model

sealed class Usuario {
    abstract val idBackend: Long
    abstract val firebaseUid: String
    abstract val email: String
    abstract val nombreUsuario: String
    abstract val fotoPerfil: String?

    data class Paciente(
        override val idBackend: Long,
        override val firebaseUid: String,
        override val email: String,
        override val nombreUsuario: String,
        override val fotoPerfil: String?
    ) : Usuario()

    data class Psicologo(
        override val idBackend: Long,
        override val firebaseUid: String,
        override val email: String,
        override val nombreUsuario: String,
        override val fotoPerfil: String?,
        val numeroColegiado: String
    ) : Usuario()
}