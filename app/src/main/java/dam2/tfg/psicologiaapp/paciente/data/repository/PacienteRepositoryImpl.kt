package dam2.tfg.psicologiaapp.paciente.data.repository

import dam2.tfg.psicologiaapp.paciente.data.remote.PacienteApi
import dam2.tfg.psicologiaapp.usuario.data.mappers.toDomain
import dam2.tfg.psicologiaapp.usuario.data.remote.AsignarPsicologoRequestDto
import dam2.tfg.psicologiaapp.usuario.domain.model.Paciente
import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PacienteRepositoryImpl @Inject constructor(
    private val pacienteApi: PacienteApi
) : PacienteRepository {

    override suspend fun listarPacientes(): Result<List<Paciente>> = runCatching {
        pacienteApi.listarPacientes().map { it.toDomain() }
    }

    override suspend fun buscarPacientes(nombreUsuario: String): Result<List<Paciente>> = runCatching {
        pacienteApi.buscarPacientes(nombreUsuario).map { it.toDomain() }
    }

    override suspend fun getPacientePorFirebase(firebaseId: String): Result<Paciente> = runCatching {
        pacienteApi.getPacientePorFirebase(firebaseId).toDomain()
    }

    override suspend fun asignarPsicologo(psicologoId: Long): Result<Paciente> = runCatching {
        pacienteApi.asignarPsicologo(AsignarPsicologoRequestDto(psicologoId = psicologoId)).toDomain()
    }
}
