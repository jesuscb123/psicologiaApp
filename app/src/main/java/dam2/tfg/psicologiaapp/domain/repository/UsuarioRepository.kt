package dam2.tfg.psicologiaapp.domain.repository

import dam2.tfg.psicologiaapp.domain.model.Usuario

interface UsuarioRepository {
    suspend fun getUsuarioByFirebaseUid(firebaeUid: String): Usuario
}