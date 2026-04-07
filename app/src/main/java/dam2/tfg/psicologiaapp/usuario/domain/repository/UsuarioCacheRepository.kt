package dam2.tfg.psicologiaapp.usuario.domain.repository

import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil

interface UsuarioCacheRepository {

    suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado?

    suspend fun guardarDesdePerfil(perfil: UsuarioPerfil)

    suspend fun limpiarCache()
}

