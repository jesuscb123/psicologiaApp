package dam2.tfg.psicologiaapp.data.repository

import dam2.tfg.psicologiaapp.data.local.dao.UsuarioDao
import dam2.tfg.psicologiaapp.data.remote.api.PsicologiaApi
import dam2.tfg.psicologiaapp.data.toDomain
import dam2.tfg.psicologiaapp.data.toEntity
import dam2.tfg.psicologiaapp.domain.model.Usuario
import dam2.tfg.psicologiaapp.domain.repository.UsuarioRepository
import jakarta.inject.Inject

class UsuarioRepositoryImpl @Inject constructor(
    private val api: PsicologiaApi,
    private val usuarioDao: UsuarioDao
) : UsuarioRepository{
    override suspend fun getUsuarioByFirebaseUid(firebaeUid: String): Usuario {
        val dto = api.getUsuarioByFirebaseUid(firebaeUid)
        val usuario = dto.toDomain()

        usuarioDao.insertUsuario(usuario.toEntity())

        return usuario
    }
}