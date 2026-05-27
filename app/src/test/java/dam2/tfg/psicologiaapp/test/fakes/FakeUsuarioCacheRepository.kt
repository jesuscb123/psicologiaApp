package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakeUsuarioCacheRepository : UsuarioCacheRepository {
    override suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado? = null
    override fun observarPerfilCacheado(): Flow<PerfilCacheado?> = emptyFlow()
    override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {}
    override suspend fun limpiarCache() {}
}
