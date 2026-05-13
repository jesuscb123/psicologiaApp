package dam2.tfg.psicologiaapp.resumenIa.domain.repository

import dam2.tfg.psicologiaapp.resumenIa.domain.model.ResumenIa

/**
 * Contrato del repositorio de resumen IA en la capa de dominio.
 *
 * La generación es bajo demanda y no se cachea: cada invocación produce un
 * resumen nuevo basado en las últimas notas del paciente.
 */
interface ResumenIaRepository {

    suspend fun generarResumenNotasPaciente(pacienteId: Long): Result<ResumenIa>
}
