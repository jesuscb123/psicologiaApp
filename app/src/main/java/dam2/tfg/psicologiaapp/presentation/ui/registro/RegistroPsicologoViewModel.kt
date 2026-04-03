package dam2.tfg.psicologiaapp.presentation.ui.registro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.CrearCuentaUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.EliminarUsuarioFirebaseActualUseCase
import dam2.tfg.psicologiaapp.psicologo.domain.model.PsicologoRequest
import dam2.tfg.psicologiaapp.usuario.domain.usecase.CrearUsuarioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroPsicologoViewModel @Inject constructor(
    private val crearCuentaUseCase: CrearCuentaUseCase,
    private val crearUsuarioUseCase: CrearUsuarioUseCase,
    private val eliminarUsuarioFirebaseActualUseCase: EliminarUsuarioFirebaseActualUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroPsicologoUiState())
    val uiState: StateFlow<RegistroPsicologoUiState> = _uiState

    fun alCambiarCorreo(nuevoCorreo: String) {
        _uiState.update { it.copy(correo = nuevoCorreo, mensajeError = null) }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _uiState.update { it.copy(contrasena = nuevaContrasena, mensajeError = null) }
    }

    fun alCambiarNombreUsuario(nuevoNombreUsuario: String) {
        _uiState.update { it.copy(nombreUsuario = nuevoNombreUsuario, mensajeError = null) }
    }

    fun alCambiarNumeroColegiado(nuevoNumeroColegiado: String) {
        _uiState.update { it.copy(numeroColegiado = nuevoNumeroColegiado, mensajeError = null) }
    }

    fun alCambiarEspecialidad(nuevaEspecialidad: String) {
        _uiState.update { it.copy(especialidad = nuevaEspecialidad, mensajeError = null) }
    }

    fun alConsumirRegistroCompletado() {
        _uiState.update { it.copy(registroCompletado = false) }
    }

    fun registrarPsicologo() {
        val correo = uiState.value.correo.trim()
        val contrasena = uiState.value.contrasena
        val nombreUsuario = uiState.value.nombreUsuario.trim()
        val numeroColegiado = uiState.value.numeroColegiado.trim()
        val especialidad = uiState.value.especialidad.trim()

        if (
            correo.isBlank() ||
            contrasena.isBlank() ||
            nombreUsuario.isBlank() ||
            numeroColegiado.isBlank() ||
            especialidad.isBlank()
        ) {
            _uiState.update { it.copy(mensajeError = "Rellena todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultadoCrearCuenta = crearCuentaUseCase(correo = correo, contrasena = contrasena)
            resultadoCrearCuenta.fold(
                onSuccess = {
                    val request = PsicologoRequest(
                        nombreUsuario = nombreUsuario,
                        fotoPerfilUrl = null,
                        numeroColegiado = numeroColegiado,
                        especialidad = especialidad
                    )

                    val resultadoCrearUsuario = crearUsuarioUseCase(request)
                    resultadoCrearUsuario.fold(
                        onSuccess = {
                            _uiState.update { it.copy(cargando = false, registroCompletado = true) }
                        },
                        onFailure = { error ->
                            val mensajeBase = error.message ?: "No se pudo completar el registro"
                            val mensajeFinal = eliminarUsuarioFirebaseActualUseCase().fold(
                                onSuccess = { mensajeBase },
                                onFailure = { eRollback ->
                                    "$mensajeBase. Además, no se pudo revertir la cuenta en Firebase: ${eRollback.message ?: "error desconocido"}"
                                }
                            )
                            _uiState.update {
                                it.copy(
                                    cargando = false,
                                    mensajeError = mensajeFinal
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

