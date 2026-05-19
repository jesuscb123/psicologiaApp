package dam2.tfg.psicologiaapp.usuario.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val usuarioId: Long,
    val firebaseUid: String,
    val nombre: String,
    val apellidos: String,
    val fotoPerfilUrl: String?,
    val rol: String,
    val email: String = "",
    val psicologoId: Long? = null,
)

