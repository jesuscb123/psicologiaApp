package dam2.tfg.psicologiaapp.presentation.ui.psicologo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoPerfil
import dam2.tfg.psicologiaapp.psicologo.domain.usecase.ActualizarDescripcionPsicologoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class AjustesPsicologoViewModel @Inject constructor(
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val actualizarDescripcionPsicologoUseCase: ActualizarDescripcionPsicologoUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AjustesPsicologoUiState())
    val uiState: StateFlow<AjustesPsicologoUiState> = _uiState

    init {
        recargar()
    }

    fun recargar() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null, mensajeOk = null) }
            getPerfilActualUseCase().fold(
                onSuccess = { perfil ->
                    if (perfil.rol != RolUsuario.PSICOLOGO) {
                        _uiState.update {
                            it.copy(cargando = false, mensajeError = "Perfil no válido para psicólogo")
                        }
                        return@launch
                    }
                    val psi = perfil as? PsicologoPerfil
                    if (psi == null) {
                        _uiState.update {
                            it.copy(cargando = false, mensajeError = "No se pudo cargar el perfil de psicólogo")
                        }
                        return@launch
                    }
                    val descripcion = psi.descripcion.orEmpty()
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            descripcion = descripcion,
                            descripcionInicial = descripcion,
                            mensajeError = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo cargar el perfil",
                        )
                    }
                }
            )
        }
    }

    fun alCambiarDescripcion(nueva: String) {
        _uiState.update { it.copy(descripcion = nueva, mensajeError = null, mensajeOk = null) }
    }

    fun guardar() {
        viewModelScope.launch {
            val actual = _uiState.value
            if (actual.guardando || !actual.hayCambios) return@launch
            _uiState.update { it.copy(guardando = true, mensajeError = null, mensajeOk = null) }

            val descripcionNormalizada = actual.descripcion.trim().takeIf { it.isNotBlank() }
            actualizarDescripcionPsicologoUseCase(descripcionNormalizada).fold(
                onSuccess = {
                    _uiState.update { estado ->
                        estado.copy(
                            guardando = false,
                            descripcionInicial = estado.descripcion,
                            mensajeOk = "Descripción actualizada",
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            guardando = false,
                            mensajeError = error.message ?: "No se pudo actualizar la descripción",
                        )
                    }
                }
            )
        }
    }

    fun limpiarMensajes() {
        _uiState.update { it.copy(mensajeError = null, mensajeOk = null) }
    }
}

