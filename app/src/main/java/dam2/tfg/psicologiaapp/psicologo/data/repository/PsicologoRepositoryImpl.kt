package dam2.tfg.psicologiaapp.psicologo.data.repository

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.data.remote.ActualizarDescripcionPsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoApi
import dam2.tfg.psicologiaapp.paciente.data.mappers.toDomain as pacienteToDomain
import dam2.tfg.psicologiaapp.psicologo.data.mappers.toDomain as psicologoToDomain
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PsicologoRepositoryImpl @Inject constructor(
    private val psicologoApi: PsicologoApi
) : PsicologoRepository {

    override suspend fun listarPsicologos(): Result<List<Psicologo>> = runCatching {
        psicologoApi.listarPsicologos().map { it.psicologoToDomain() }
    }

    override suspend fun buscarPsicologos(nombreUsuario: String): Result<List<Psicologo>> = runCatching {
        psicologoApi.buscarPsicologos(nombreUsuario).map { it.psicologoToDomain() }
    }

    override suspend fun getPsicologoPorFirebase(firebaseId: String): Result<Psicologo> = runCatching {
        psicologoApi.getPsicologoPorFirebase(firebaseId).psicologoToDomain()
    }

    override suspend fun getPacientesDePsicologo(): Result<List<Paciente>> = runCatching {
        val respuesta = psicologoApi.getPacientesDePsicologo()
        if (!respuesta.isSuccessful) {
            throw IllegalStateException("Error al obtener pacientes: HTTP ${respuesta.code()}")
        }
        if (respuesta.code() == 204 || respuesta.body() == null) {
            emptyList()
        } else {
            respuesta.body()!!.map { it.pacienteToDomain() }
        }
    }

    override suspend fun actualizarMiDescripcion(descripcion: String?): Result<Psicologo> = runCatching {
        psicologoApi.actualizarMiDescripcion(
            ActualizarDescripcionPsicologoRequestDto(descripcion = descripcion)
        ).psicologoToDomain()
    }
}
