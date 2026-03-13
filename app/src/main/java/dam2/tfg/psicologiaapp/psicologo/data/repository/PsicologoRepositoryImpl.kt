package dam2.tfg.psicologiaapp.psicologo.data.repository

import dam2.tfg.psicologiaapp.usuario.domain.model.Paciente
import dam2.tfg.psicologiaapp.usuario.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoApi
import dam2.tfg.psicologiaapp.usuario.data.mappers.toDomain
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PsicologoRepositoryImpl @Inject constructor(
    private val psicologoApi: PsicologoApi
) : PsicologoRepository {

    override suspend fun listarPsicologos(): Result<List<Psicologo>> = runCatching {
        psicologoApi.listarPsicologos().map { it.toDomain() }
    }

    override suspend fun buscarPsicologos(nombreUsuario: String): Result<List<Psicologo>> = runCatching {
        psicologoApi.buscarPsicologos(nombreUsuario).map { it.toDomain() }
    }

    override suspend fun getPsicologoPorFirebase(firebaseId: String): Result<Psicologo> = runCatching {
        psicologoApi.getPsicologoPorFirebase(firebaseId).toDomain()
    }

    override suspend fun getPacientesDePsicologo(): Result<List<Paciente>> = runCatching {
        psicologoApi.getPacientesDePsicologo().map { it.toDomain() }
    }
}
