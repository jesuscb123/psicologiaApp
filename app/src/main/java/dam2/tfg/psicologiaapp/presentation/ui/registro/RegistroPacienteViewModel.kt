package dam2.tfg.psicologiaapp.presentation.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.CrearCuentaUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacienteRequest
import dam2.tfg.psicologiaapp.usuario.domain.usecase.CrearUsuarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroPacienteViewModel @Inject constructor(
    private val crearCuentaUseCase: CrearCuentaUseCase,
    private val crearUsuarioUseCase: CrearUsuarioUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroPacienteUiState())
    val uiState: StateFlow<RegistroPacienteUiState> = _uiState

    fun alCambiarCorreo(nuevoCorreo: String) {
        _uiState.update { it.copy(correo = nuevoCorreo, mensajeError = null) }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _uiState.update { it.copy(contrasena = nuevaContrasena, mensajeError = null) }
    }

    fun alCambiarNombreUsuario(nuevoNombreUsuario: String) {
        _uiState.update { it.copy(nombreUsuario = nuevoNombreUsuario, mensajeError = null) }
    }

    fun alConsumirRegistroCompletado() {
        _uiState.update { it.copy(registroCompletado = false) }
    }

    fun registrarPaciente() {
        val correo = uiState.value.correo.trim()
        val contrasena = uiState.value.contrasena
        val nombreUsuario = uiState.value.nombreUsuario.trim()

        if (correo.isBlank() || contrasena.isBlank() || nombreUsuario.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Rellena todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultadoCrearCuenta = crearCuentaUseCase(correo = correo, contrasena = contrasena)
            resultadoCrearCuenta.fold(
                onSuccess = {
                    val request = PacienteRequest(
                        nombreUsuario = nombreUsuario,
                        fotoPerfilUrl = null,
                        psicologoId = null
                    )

                    val resultadoCrearUsuario = crearUsuarioUseCase(request)
                    resultadoCrearUsuario.fold(
                        onSuccess = {
                            _uiState.update { it.copy(cargando = false, registroCompletado = true) }
                        },
                        onFailure = { error ->
                            _uiState.update {
                                it.copy(
                                    cargando = false,
                                    mensajeError = error.message ?: "No se pudo completar el registro"
                                )
                            }
                        }
                    )
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = error.message ?: "No se pudo crear la cuenta"
                        )
                    }
                }
            )
        }
    }
}

