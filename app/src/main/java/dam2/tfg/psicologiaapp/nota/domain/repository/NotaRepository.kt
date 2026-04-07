package dam2.tfg.psicologiaapp.nota.domain.repository

import dam2.tfg.psicologiaapp.nota.domain.model.Nota
import kotlinx.coroutines.flow.Flow

/**
 * Contrato del repositorio de notas en dominio.
 */
interface NotaRepository {

    suspend fun getNotasPacienteActual(): Result<List<Nota>>

    suspend fun getNotasDePaciente(pacienteId: Long): Result<List<Nota>>

    fun observarNotasPacienteActual(): Flow<List<Nota>>

    fun observarNotasDePaciente(pacienteId: Long): Flow<List<Nota>>

    suspend fun sincronizarNotasPacienteActual(): Result<Unit>

    suspend fun sincronizarNotasDePaciente(pacienteId: Long): Result<Unit>

    suspend fun crearNota(asunto: String, descripcion: String): Result<Nota>

    suspend fun actualizarNota(notaId: Long, asunto: String, descripcion: String): Result<Nota>

    suspend fun borrarNota(notaId: Long): Result<Unit>
}
