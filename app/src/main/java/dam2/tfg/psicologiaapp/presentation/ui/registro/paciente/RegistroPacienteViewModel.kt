package dam2.tfg.psicologiaapp.presentation.ui.registro.paciente

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.CrearCuentaUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.EliminarUsuarioFirebaseActualUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacienteRequest
import dam2.tfg.psicologiaapp.presentation.ui.registro.util.LimitesCaracteresRegistro
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
    private val crearUsuarioUseCase: CrearUsuarioUseCase,
    private val eliminarUsuarioFirebaseActualUseCase: EliminarUsuarioFirebaseActualUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroPacienteUiState())
    val uiState: StateFlow<RegistroPacienteUiState> = _uiState

    fun alCambiarCorreo(nuevoCorreo: String) {
        val max = LimitesCaracteresRegistro.Paciente.CORREO
        if (nuevoCorreo.length > max) {
            _uiState.update {
                it.copy(
                    correo = nuevoCorreo.take(max),
                    errorLongitudCorreo = LimitesCaracteresRegistro.Paciente.mensajeMaximoCaracteres(max),
                    mensajeError = null
                )
            }
        } else {
            _uiState.update { it.copy(correo = nuevoCorreo, errorLongitudCorreo = null, mensajeError = null) }
        }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _uiState.update { it.copy(contrasena = nuevaContrasena, mensajeError = null) }
    }

    fun alCambiarNombre(nuevoNombre: String) {
        val max = LimitesCaracteresRegistro.Paciente.NOMBRE
        if (nuevoNombre.length > max) {
            _uiState.update {
                it.copy(
                    nombre = nuevoNombre.take(max),
                    errorLongitudNombre = LimitesCaracteresRegistro.Paciente.mensajeMaximoCaracteres(max),
                    mensajeError = null
                )
            }
        } else {
            _uiState.update { it.copy(nombre = nuevoNombre, errorLongitudNombre = null, mensajeError = null) }
        }
    }

    fun alCambiarApellidos(nuevosApellidos: String) {
        val max = LimitesCaracteresRegistro.Paciente.APELLIDOS
        if (nuevosApellidos.length > max) {
            _uiState.update {
                it.copy(
                    apellidos = nuevosApellidos.take(max),
                    errorLongitudApellidos = LimitesCaracteresRegistro.Paciente.mensajeMaximoCaracteres(max),
                    mensajeError = null
                )
            }
        } else {
            _uiState.update {
                it.copy(apellidos = nuevosApellidos, errorLongitudApellidos = null, mensajeError = null)
            }
        }
    }

    fun alConsumirRegistroCompletado() {
        _uiState.update { it.copy(registroCompletado = false) }
    }

    fun registrarPaciente() {
        val correo = uiState.value.correo.trim()
        val contrasena = uiState.value.contrasena
        val nombre = uiState.value.nombre.trim()
        val apellidos = uiState.value.apellidos.trim()

        if (correo.isBlank() || contrasena.isBlank() || nombre.isBlank() || apellidos.isBlank()) {
            _uiState.update { it.copy(mensajeError = "Rellena todos los campos") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(cargando = true, mensajeError = null) }

            val resultadoCrearCuenta = crearCuentaUseCase(correo = correo, contrasena = contrasena)
            resultadoCrearCuenta.fold(
                onSuccess = {
                    val request = PacienteRequest(
                        nombre = nombre,
                        apellidos = apellidos,
                        fotoPerfilUrl = null,
                        psicologoId = null
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
