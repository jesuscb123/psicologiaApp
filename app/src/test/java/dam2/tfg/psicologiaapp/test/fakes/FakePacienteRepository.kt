package dam2.tfg.psicologiaapp.test.fakes

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import dam2.tfg.psicologiaapp.paciente.domain.repository.PacienteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

open class FakePacienteRepository : PacienteRepository {
    override suspend fun listarPacientes(): Result<List<Paciente>> = Result.success(emptyList())
    override suspend fun buscarPacientes(nombreUsuario: String): Result<List<Paciente>> = Result.success(emptyList())
    override suspend fun getPacientePorFirebase(firebaseId: String): Result<Paciente> = Result.failure(NotImplementedError())
    override suspend fun asignarPsicologo(psicologoId: Long): Result<Paciente> = Result.failure(NotImplementedError())
    override suspend fun cancelarTerapia(): Result<Paciente> = Result.failure(NotImplementedError())
    override fun observarPacientes(): Flow<List<Paciente>> = emptyFlow()
}
