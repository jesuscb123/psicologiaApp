package dam2.tfg.psicologiaapp.notificaciones.data.repository

import dam2.tfg.psicologiaapp.notificaciones.data.remote.AlertaRiesgoFuenteDatosFirebase
import dam2.tfg.psicologiaapp.notificaciones.domain.repository.AlertaRiesgoRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AlertaRiesgoRepositoryImpl @Inject constructor(
    private val fuenteDatos: AlertaRiesgoFuenteDatosFirebase,
) : AlertaRiesgoRepository {

    override fun observarAlertasRiesgo(psicologoUid: String): Flow<Set<Long>> =
        fuenteDatos.observarAlertasRiesgo(psicologoUid)

    override suspend fun marcarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {
        fuenteDatos.marcarAlertaRiesgo(psicologoUid, pacienteId)
    }

    override suspend fun limpiarAlertaRiesgo(psicologoUid: String, pacienteId: Long) {
        fuenteDatos.limpiarAlertaRiesgo(psicologoUid, pacienteId)
    }
}
