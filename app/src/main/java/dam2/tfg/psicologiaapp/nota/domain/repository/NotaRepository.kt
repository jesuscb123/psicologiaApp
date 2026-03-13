package dam2.tfg.psicologiaapp.nota.domain.repository

import dam2.tfg.psicologiaapp.nota.domain.model.Nota

/**
 * Contrato del repositorio de notas en dominio.
 */
interface NotaRepository {

    suspend fun getNotasPacienteActual(): Result<List<Nota>>

    suspend fun getNotasDePaciente(pacienteId: Long): Result<List<Nota>>

    suspend fun crearNota(firebaseId: String, asunto: String, descripcion: String): Result<Nota>

    suspend fun actualizarNota(notaId: Long, asunto: String, descripcion: String): Result<Nota>

    suspend fun borrarNota(notaId: Long): Result<Unit>
}
