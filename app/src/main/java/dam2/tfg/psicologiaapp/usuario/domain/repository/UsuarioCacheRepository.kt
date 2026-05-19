package dam2.tfg.psicologiaapp.usuario.domain.repository

import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import kotlinx.coroutines.flow.Flow

interface UsuarioCacheRepository {

    suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado?

    fun observarPerfilCacheado(): Flow<PerfilCacheado?>

    suspend fun guardarDesdePerfil(perfil: UsuarioPerfil)

    suspend fun limpiarCache()
}

