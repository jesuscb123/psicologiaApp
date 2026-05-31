package dam2.tfg.psicologiaapp.paciente.data.repository

import dam2.tfg.psicologiaapp.paciente.data.local.PacienteDao
import dam2.tfg.psicologiaapp.paciente.data.mappers.toDomain
import dam2.tfg.psicologiaapp.paciente.data.mappers.toEntity
import dam2.tfg.psicologiaapp.paciente.data.remote.AsignarPsicologoRequestDto
import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteApi
import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import dam2.tfg.psicologiaapp.util.runSuspendCatching
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Singleton
class PacienteRepositoryImpl @Inject constructor(
    private val pacienteApi: PacienteApi,
    private val pacienteDao: PacienteDao,
) : PacienteRepository {

    override suspend fun listarPacientes(): Result<List<Paciente>> = runSuspendCatching {
        val lista = pacienteApi.listarPacientes()
        pacienteDao.borrarTodos()
        pacienteDao.guardarTodos(lista.map { it.toEntity() })
        lista.map { it.toDomain() }
    }

    override suspend fun buscarPacientes(nombreUsuario: String): Result<List<Paciente>> = runSuspendCatching {
        pacienteApi.buscarPacientes(nombreUsuario).map { it.toDomain() }
    }

    override suspend fun getPacientePorFirebase(firebaseId: String): Result<Paciente> = runSuspendCatching {
        pacienteApi.getPacientePorFirebase(firebaseId).toDomain()
    }

    override suspend fun asignarPsicologo(psicologoId: Long): Result<Paciente> = runSuspendCatching {
        pacienteApi.asignarPsicologo(AsignarPsicologoRequestDto(psicologoId = psicologoId)).toDomain()
    }

    override suspend fun cancelarTerapia(): Result<Paciente> = runSuspendCatching {
        pacienteApi.cancelarTerapia().toDomain()
    }

    override fun observarPacientes(): Flow<List<Paciente>> =
        pacienteDao.observarTodos().map { lista -> lista.map { it.toDomain() } }
}
