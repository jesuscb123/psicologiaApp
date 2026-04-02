package dam2.tfg.psicologiaapp.presentation.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.IniciarSesionUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IniciarSesionViewModel @Inject constructor(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val getPerfilActualUseCase: GetPerfilActualUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(IniciarSesionUiState())
    val uiState: StateFlow<IniciarSesionUiState> = _uiState

    fun alCambiarCorreo(nuevoCorreo: String) {
        _uiState.update { it.copy(correo = nuevoCorreo, mensajeError = null) }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _uiState.update { it.copy(contrasena = nuevaContrasena, mensajeError = null) }
    }

    fun alConsumirEventoNavegacion() {
        _uiState.update { it.copy(eventoNavegacion = null) }
    }

    fun iniciarSesion() {
        val correo = uiState.value.correo.trim()
        val contrasena = uiState.value.contrasena

        if (correo.isBlank() || contrasena.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Rellena correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultadoLogin = iniciarSesionUseCase(correo = correo, contrasena = contrasena)
            resultadoLogin.fold(
                onSuccess = {
                    val resultadoPerfil = getPerfilActualUseCase()
                    resultadoPerfil.fold(
                        onSuccess = { perfil ->
                            when (perfil.rol) {
                                RolUsuario.PACIENTE -> _uiState.update {
                                    it.copy(cargando = false, eventoNavegacion = EventoNavegacionIniciarSesion.IrAHomePaciente)
                                }
                                RolUsuario.PSICOLOGO -> _uiState.update {
                                    it.copy(cargando = false, eventoNavegacion = EventoNavegacionIniciarSesion.IrAHomePsicologo)
                                }
                                RolUsuario.SIN_ROL -> _uiState.update {
                                    it.copy(cargando = false, eventoNavegacion = EventoNavegacionIniciarSesion.IrARegistro)
                                }
                            }
                        },
                        onFailure = {
                            _uiState.update {
                                it.copy(cargando = false, eventoNavegacion = EventoNavegacionIniciarSesion.IrARegistro)
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo iniciar sesión"
                        )
                    }
                }
            )
        }
    }
}

