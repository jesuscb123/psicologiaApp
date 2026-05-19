package dam2.tfg.psicologiaapp.usuario.data.repository

import dam2.tfg.psicologiaapp.usuario.data.local.UsuarioDao
import dam2.tfg.psicologiaapp.usuario.data.mappers.toEntityCache
import dam2.tfg.psicologiaapp.usuario.data.mappers.toPerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class UsuarioCacheRepositoryImpl @Inject constructor(
    private val usuarioDao: UsuarioDao,
) : UsuarioCacheRepository {

    override suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado? =
        usuarioDao.obtenerPorFirebaseUid(firebaseUid)?.toPerfilCacheado()

    override fun observarPerfilCacheado(): Flow<PerfilCacheado?> =
        usuarioDao.observarPrimero().map { it?.toPerfilCacheado() }

    override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {
        usuarioDao.guardar(perfil.toEntityCache())
    }

    override suspend fun limpiarCache() {
        usuarioDao.borrarTodos()
    }
}

