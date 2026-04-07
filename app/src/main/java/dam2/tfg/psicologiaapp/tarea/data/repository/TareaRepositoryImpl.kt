package dam2.tfg.psicologiaapp.tarea.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import dam2.tfg.psicologiaapp.data.remote.ProveedorTokenFirebase
import dam2.tfg.psicologiaapp.tarea.data.local.TareaDao
import dam2.tfg.psicologiaapp.tarea.data.mappers.toDomain
import dam2.tfg.psicologiaapp.tarea.data.mappers.toEntity
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaActualizarRealizadaRequestDto
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaActualizarRequestDto
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaApi
import dam2.tfg.psicologiaapp.tarea.data.remote.TareaCrearRequestDto
import dam2.tfg.psicologiaapp.tarea.domain.model.Tarea
import dam2.tfg.psicologiaapp.tarea.domain.repository.TareaRepository
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

@Singleton
class TareaRepositoryImpl @Inject constructor(
    private val tareaApi: TareaApi,
    private val proveedorTokenFirebase: ProveedorTokenFirebase,
    private val tareaDao: TareaDao,
    private val preferenciasDataStore: DataStore<Preferences>,
) : TareaRepository {

    override suspend fun getTareasPacienteActual(): Result<List<Tarea>> = runCatching {
        sincronizarTareasPacienteActualSiProcede()
        tareaDao.listarTodas().map { it.toDomain() }
    }

    override suspend fun getTareasDePaciente(pacienteId: Long): Result<List<Tarea>> = runCatching {
        sincronizarTareasDePacienteSiProcede(pacienteId)
        tareaDao.listarPorPacienteId(pacienteId).map { it.toDomain() }
    }

    override fun observarTareasPacienteActual(): Flow<List<Tarea>> =
        tareaDao.observarTodas().map { lista -> lista.map { it.toDomain() } }

    override fun observarTareasDePaciente(pacienteId: Long): Flow<List<Tarea>> =
        tareaDao.observarPorPacienteId(pacienteId).map { lista -> lista.map { it.toDomain() } }

    override suspend fun sincronizarTareasPacienteActual(): Result<Unit> = runCatching {
        sincronizarTareasPacienteActualSiProcede()
    }

    override suspend fun sincronizarTareasDePaciente(pacienteId: Long): Result<Unit> = runCatching {
        sincronizarTareasDePacienteSiProcede(pacienteId)
    }

    override suspend fun crearTarea(
        pacienteId: Long,
        titulo: String,
        descripcion: String
    ): Result<Tarea> = runCatching {
        val creada = tareaApi.crearTarea(
            pacienteId,
            TareaCrearRequestDto(titulo = titulo, descripcion = descripcion)
        )
        tareaDao.guardar(creada.toEntity())
        actualizarEstadoLocalPacienteActualDesdeRemoto()
        creada.toDomain()
    }

    override suspend fun marcarRealizada(tareaId: Long, realizada: Boolean): Result<Tarea> = runCatching {
        val actualizada = tareaApi.marcarRealizada(tareaId, TareaActualizarRealizadaRequestDto(realizada = realizada))
        tareaDao.guardar(actualizada.toEntity())
        actualizarEstadoLocalPacienteActualDesdeRemoto()
        actualizada.toDomain()
    }

    override suspend fun aceptarTarea(tareaId: Long): Result<Tarea> = runCatching {
        val actualizada = tareaApi.aceptarTarea(tareaId)
        tareaDao.guardar(actualizada.toEntity())
        actualizarEstadoLocalPacienteActualDesdeRemoto()
        actualizada.toDomain()
    }

    override suspend fun actualizarTarea(
        tareaId: Long,
        titulo: String,
        descripcion: String,
        realizada: Boolean
    ): Result<Tarea> = runCatching {
        val actualizada = tareaApi.actualizarTarea(
            tareaId,
            TareaActualizarRequestDto(titulo = titulo, descripcion = descripcion, realizada = realizada)
        )
        tareaDao.guardar(actualizada.toEntity())
        actualizarEstadoLocalPacienteActualDesdeRemoto()
        actualizada.toDomain()
    }

    override suspend fun eliminarTarea(tareaId: Long): Result<Unit> = runCatching {
        tareaApi.eliminarTarea(tareaId)
        tareaDao.borrarPorId(tareaId)
        actualizarEstadoLocalPacienteActualDesdeRemoto()
    }

    private suspend fun sincronizarTareasPacienteActualSiProcede() {
        val respuestaEstado = ejecutarConRefrescoTokenSi401 {
            tareaApi.getEstadoTareasPacienteActual()
        }
        if (!respuestaEstado.isSuccessful) {
            throw IllegalStateException("Error al obtener estado de tareas: HTTP ${respuestaEstado.code()}")
        }

        val estadoRemoto = respuestaEstado.body()
        val ultimaRemota = estadoRemoto?.ultimaModificacion
        val totalRemoto = estadoRemoto?.total ?: 0L

        val ultimaLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveUltimaPacienteActual) }
        val totalLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveTotalPacienteActual) } ?: -1L

        val sinCambios = (ultimaLocal == ultimaRemota) && (totalLocal == totalRemoto)
        if (sinCambios) return

        val respuestaLista = ejecutarConRefrescoTokenSi401 {
            tareaApi.getTareasPacienteActual()
        }
        if (respuestaLista.code() == 204) {
            tareaDao.borrarTodas()
            guardarEstadoPacienteActual(ultimaRemota, totalRemoto)
            return
        }
        if (!respuestaLista.isSuccessful) {
            throw IllegalStateException("Error al obtener tareas: HTTP ${respuestaLista.code()}")
        }

        val tareas = respuestaLista.body().orEmpty()
        tareaDao.borrarTodas()
        tareaDao.guardarTodas(tareas.map { it.toEntity() })
        guardarEstadoPacienteActual(ultimaRemota, totalRemoto)
    }

    private suspend fun sincronizarTareasDePacienteSiProcede(pacienteId: Long) {
        val respuestaEstado = ejecutarConRefrescoTokenSi401 {
            tareaApi.getEstadoTareasDePaciente(pacienteId)
        }
        if (!respuestaEstado.isSuccessful) {
            throw IllegalStateException("Error al obtener estado de tareas: HTTP ${respuestaEstado.code()}")
        }

        val estadoRemoto = respuestaEstado.body()
        val ultimaRemota = estadoRemoto?.ultimaModificacion
        val totalRemoto = estadoRemoto?.total ?: 0L

        val claveUltima = claveUltimaPorPacienteId(pacienteId)
        val claveTotal = claveTotalPorPacienteId(pacienteId)

        val ultimaLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveUltima) }
        val totalLocal = preferenciasDataStore.data
            .let { flujo -> flujo.firstOrNullPreferencia(claveTotal) } ?: -1L

        val sinCambios = (ultimaLocal == ultimaRemota) && (totalLocal == totalRemoto)
        if (sinCambios) return

        val respuestaLista = ejecutarConRefrescoTokenSi401 {
            tareaApi.getTareasDePaciente(pacienteId)
        }
        if (respuestaLista.code() == 204) {
            tareaDao.borrarPorPacienteId(pacienteId)
            guardarEstadoPorPacienteId(pacienteId, ultimaRemota, totalRemoto)
            return
        }
        if (!respuestaLista.isSuccessful) {
            throw IllegalStateException("Error al obtener tareas: HTTP ${respuestaLista.code()}")
        }

        val tareas = respuestaLista.body().orEmpty()
        tareaDao.borrarPorPacienteId(pacienteId)
        tareaDao.guardarTodas(tareas.map { it.toEntity() })
        guardarEstadoPorPacienteId(pacienteId, ultimaRemota, totalRemoto)
    }

    private suspend fun actualizarEstadoLocalPacienteActualDesdeRemoto() {
        val respuestaEstado = ejecutarConRefrescoTokenSi401 {
            tareaApi.getEstadoTareasPacienteActual()
        }
        if (!respuestaEstado.isSuccessful) return
        val estado = respuestaEstado.body()
        guardarEstadoPacienteActual(estado?.ultimaModificacion, estado?.total ?: 0L)
    }

    private suspend fun guardarEstadoPacienteActual(ultima: String?, total: Long) {
        preferenciasDataStore.edit { preferencias ->
            preferencias[claveUltimaPacienteActual] = ultima.orEmpty()
            preferencias[claveTotalPacienteActual] = total
        }
    }

    private suspend fun guardarEstadoPorPacienteId(pacienteId: Long, ultima: String?, total: Long) {
        val claveUltima = claveUltimaPorPacienteId(pacienteId)
        val claveTotal = claveTotalPorPacienteId(pacienteId)
        preferenciasDataStore.edit { preferencias ->
            preferencias[claveUltima] = ultima.orEmpty()
            preferencias[claveTotal] = total
        }
    }

    private fun claveUltimaPorPacienteId(pacienteId: Long) =
        stringPreferencesKey("sync_tareas_paciente_${pacienteId}_ultima_modificacion")

    private fun claveTotalPorPacienteId(pacienteId: Long) =
        longPreferencesKey("sync_tareas_paciente_${pacienteId}_total")

    private suspend fun <T> ejecutarConRefrescoTokenSi401(
        bloque: suspend () -> retrofit2.Response<T>
    ): retrofit2.Response<T> {
        var respuesta = bloque()
        if (respuesta.code() == 401) {
            proveedorTokenFirebase.obtenerToken(forzarRenovacion = true)
            respuesta = bloque()
        }
        return respuesta
    }

    private suspend fun <T> kotlinx.coroutines.flow.Flow<Preferences>.firstOrNullPreferencia(
        clave: Preferences.Key<T>
    ): T? = try {
        this.first()[clave]
    } catch (_: Throwable) {
        null
    }

    private companion object {
        val claveUltimaPacienteActual =
            stringPreferencesKey("sync_tareas_paciente_actual_ultima_modificacion")
        val claveTotalPacienteActual =
            longPreferencesKey("sync_tareas_paciente_actual_total")
    }
}
