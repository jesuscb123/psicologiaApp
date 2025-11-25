package dam2.tfg.psicologiaapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class UsuarioEntity(
    @PrimaryKey val firebaseUid: String,
    val idBackend: Long?,
    val email: String,
    val nombreUsuario: String
)