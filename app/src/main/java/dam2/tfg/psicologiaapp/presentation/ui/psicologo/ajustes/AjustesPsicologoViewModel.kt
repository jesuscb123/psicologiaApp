package dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ActualizarDescripcionPsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ActualizarEspecialidadesPsicologoUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ObservarPsicologosUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.SincronizarPsicologosUseCase
import dam2.tfg.psicologiaapp.presentation.ui.registro.util.LimitesCaracteresRegistro
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AjustesPsicologoViewModel @Inject constructor(
    private val observarPerfilCacheadoUseCase: ObservarPerfilCacheadoUseCase,
    private val observarPsicologosUseCase: ObservarPsicologosUseCase,
    private val sincronizarPsicologosUseCase: SincronizarPsicologosUseCase,
    private val sincronizarPerfilActualUseCase: SincronizarPerfilActualUseCase,
    private val actualizarDescripcionPsicologoUseCase: ActualizarDescripcionPsicologoUseCase,
    private val actualizarEspecialidadesPsicologoUseCase: ActualizarEspecialidadesPsicologoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesPsicologoUiState())
    val uiState: StateFlow<AjustesPsicologoUiState> = _uiState

    init {
        // Observa Room combinando perfil cacheado (para usuarioId) + lista de psicólogos.
        // Emite en cuanto Room tiene datos, sin necesidad de red.
        viewModelScope.launch {
            combine(
                observarPerfilCacheadoUseCase(),
                observarPsicologosUseCase(),
            ) { perfil, lista ->
                val uid = perfil?.usuarioId ?: return@combine null
                lista.firstOrNull { it.usuarioId == uid }
            }
                .filterNotNull()
                .collectLatest { psi ->
                    val descripcion = psi.descripcion.orEmpty()
                    _uiState.update { state ->
                        if (!state.yaSeHaCargado) {
                            state.copy(
                                cargando = false,
                                yaSeHaCargado = true,
                                descripcion = descripcion,
                                descripcionInicial = descripcion,
                                especialidades = psi.especialidades,
                                especialidadesIniciales = psi.especialidades,
                                mensajeError = null,
                            )
                        } else {
                            // Sincronización posterior: preservar ediciones en curso
                            state.copy(
                                descripcion = if (state.descripcion == state.descripcionInicial) descripcion else state.descripcion,
                                descripcionInicial = descripcion,
                                especialidades = if (state.especialidades == state.especialidadesIniciales) psi.especialidades else state.especialidades,
                                especialidadesIniciales = psi.especialidades,
                            )
                        }
                    }
                }
        }

        sincronizarEnBackground()
    }

    private fun sincronizarEnBackground() {
        viewModelScope.launch {
            val hayDatos = _uiState.value.yaSeHaCargado
            if (!hayDatos) {
                _uiState.update { it.copy(cargando = true, mensajeError = null) }
            }
            sincronizarPerfilActualUseCase()
            val resultado = sincronizarPsicologosUseCase()
            resultado.onFailure { error ->
                if (!_uiState.value.yaSeHaCargado) {
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cargar el perfil",
                        )
                    }
                }
            }
            if (!_uiState.value.yaSeHaCargado) {
                _uiState.update { it.copy(cargando = false) }
            }
        }
    }

    fun alCambiarDescripcion(nueva: String) {
        _uiState.update { it.copy(descripcion = nueva, mensajeError = null, mensajeOk = null) }
    }

    fun alCambiarEspecialidadInput(nueva: String) {
        if (nueva.length <= LimitesCaracteresRegistro.Psicologo.ESPECIALIDAD) {
            _uiState.update { it.copy(especialidadInput = nueva, errorEspecialidadInput = null) }
        }
    }

    fun alAnadirEspecialidad() {
        val texto = _uiState.value.especialidadInput.trim()
        val lista = _uiState.value.especialidades

        if (texto.isBlank()) {
            _uiState.update { it.copy(errorEspecialidadInput = "Escribe una especialidad antes de añadir") }
            return
        }
        if (lista.size >= LimitesCaracteresRegistro.Psicologo.MAX_ESPECIALIDADES) {
            _uiState.update { it.copy(errorEspecialidadInput = "Máximo ${LimitesCaracteresRegistro.Psicologo.MAX_ESPECIALIDADES} especialidades") }
            return
        }
        if (lista.any { it.equals(texto, ignoreCase = true) }) {
            _uiState.update { it.copy(errorEspecialidadInput = "Ya existe esa especialidad") }
            return
        }

        _uiState.update {
            it.copy(
                especialidades = lista + texto,
                especialidadInput = "",
                errorEspecialidadInput = null,
                mensajeError = null,
            )
        }
    }

    fun alEliminarEspecialidad(index: Int) {
        val lista = _uiState.value.especialidades.toMutableList()
        if (index in lista.indices) {
            lista.removeAt(index)
            _uiState.update { it.copy(especialidades = lista, mensajeError = null) }
        }
    }

    fun guardar() {
        viewModelScope.launch {
            val actual = _uiState.value
            if (actual.guardando || !actual.hayCambios) return@launch
            _uiState.update { it.copy(guardando = true, mensajeError = null, mensajeOk = null) }

            val descripcionCambiada = actual.descripcion != actual.descripcionInicial
            val especialidadesCambiadas = actual.especialidades != actual.especialidadesIniciales

            var error: String? = null

            if (descripcionCambiada) {
                val descripcionNormalizada = actual.descripcion.trim().takeIf { it.isNotBlank() }
                actualizarDescripcionPsicologoUseCase(descripcionNormalizada).onFailure { e ->
                    error = e.message ?: "No se pudo actualizar la descripción"
                }
            }

            if (especialidadesCambiadas && error == null) {
                actualizarEspecialidadesPsicologoUseCase(actual.especialidades).onFailure { e ->
                    error = e.message ?: "No se pudieron actualizar las especialidades"
                }
            }

            if (error != null) {
                _uiState.update { it.copy(guardando = false, mensajeError = error) }
            } else {
                _uiState.update { estado ->
                    estado.copy(
                        guardando = false,
                        descripcionInicial = estado.descripcion,
                        especialidadesIniciales = estado.especialidades,
                        mensajeOk = "Cambios guardados",
                    )
                }
            }
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeOk = null) }
    }
}
