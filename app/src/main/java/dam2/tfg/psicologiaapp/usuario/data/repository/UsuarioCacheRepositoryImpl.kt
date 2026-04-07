package dam2.tfg.psicologiaapp.usuario.data.repository

import dam2.tfg.psicologiaapp.data.local.PsicologiaAppDatabase
import dam2.tfg.psicologiaapp.usuario.data.mappers.toEntityCache
import dam2.tfg.psicologiaapp.usuario.data.mappers.toPerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.PerfilCacheado
import dam2.tfg.psicologiaapp.usuario.domain.model.UsuarioPerfil
import dam2.tfg.psicologiaapp.usuario.domain.repository.UsuarioCacheRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsuarioCacheRepositoryImpl @Inject constructor(
    private val baseDeDatos: PsicologiaAppDatabase,
) : UsuarioCacheRepository {

    override suspend fun obtenerPerfilCacheadoPorFirebaseUid(firebaseUid: String): PerfilCacheado? =
        baseDeDatos.usuarioDao().obtenerPorFirebaseUid(firebaseUid)?.toPerfilCacheado()

    override suspend fun guardarDesdePerfil(perfil: UsuarioPerfil) {
        baseDeDatos.usuarioDao().guardar(perfil.toEntityCache())
    }

    override suspend fun limpiarCache() {
        baseDeDatos.usuarioDao().borrarTodos()
    }
}

