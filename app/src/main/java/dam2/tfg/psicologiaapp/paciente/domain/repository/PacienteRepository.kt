package dam2.tfg.psicologiaapp.paciente.domain.repository

import dam2.tfg.psicologiaapp.paciente.domain.model.Paciente
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de pacientes en dominio.
 */
interface PacienteRepository {

    suspend fun listarPacientes(): Result<List<Paciente>>

    suspend fun buscarPacientes(nombreUsuario: String): Result<List<Paciente>>

    suspend fun getPacientePorFirebase(firebaseId: String): Result<Paciente>

    suspend fun asignarPsicologo(psicologoId: Long): Result<Paciente>

    suspend fun cancelarTerapia(): Result<Paciente>

    fun observarPacientes(): Flow<List<Paciente>>
}
