package dam2.tfg.psicologiaapp.psicologo.data.repository

import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.psicologo.data.local.PsicologoDao
import dam2.tfg.psicologiaapp.psicologo.domain.model.Psicologo
import dam2.tfg.psicologiaapp.psicologo.data.remote.ActualizarDescripcionPsicologoRequestDto
import dam2.tfg.psicologiaapp.psicologo.data.remote.PsicologoApi
import dam2.tfg.psicologiaapp.paciente.data.mappers.toDomain as pacienteToDomain
import dam2.tfg.psicologiaapp.paciente.data.mappers.toEntity as pacienteToEntity
import dam2.tfg.psicologiaapp.psicologo.data.mappers.toDomain as psicologoToDomain
import dam2.tfg.psicologiaapp.psicologo.data.mappers.toEntity as psicologoToEntity
import dam2.tfg.psicologiaapp.psicologo.domain.repository.PsicologoRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class PsicologoRepositoryImpl @Inject constructor(
    private val psicologoApi: PsicologoApi,
    private val psicologoDao: PsicologoDao,
    private val pacienteDao: PacienteDao,
) : PsicologoRepository {

    override suspend fun listarPsicologos(): Result<List<Psicologo>> = runCatching {
        val lista = psicologoApi.listarPsicologos()
        psicologoDao.borrarTodos()
        psicologoDao.guardarTodos(lista.map { it.psicologoToEntity() })
        lista.map { it.psicologoToDomain() }
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
        val lista = if (respuesta.code() == 204 || respuesta.body() == null) {
            emptyList()
        } else {
            respuesta.body()!!
        }
        pacienteDao.borrarTodos()
        pacienteDao.guardarTodos(lista.map { it.pacienteToEntity() })
        lista.map { it.pacienteToDomain() }
    }

    override suspend fun actualizarMiDescripcion(descripcion: String?): Result<Psicologo> = runCatching {
        psicologoApi.actualizarMiDescripcion(
            ActualizarDescripcionPsicologoRequestDto(descripcion = descripcion)
        ).psicologoToDomain()
    }

    override fun observarPsicologos(): Flow<List<Psicologo>> =
        psicologoDao.observarTodos().map { lista -> lista.map { it.psicologoToDomain() } }

    override fun observarPacientesDePsicologo(): Flow<List<Paciente>> =
        pacienteDao.observarTodos().map { lista -> lista.map { it.pacienteToDomain() } }
}
