package dam2.tfg.psicologiaapp.presentation.ui.registro.psicologo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.CrearCuentaUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.EliminarUsuarioFirebaseActualUseCase
import dam2.tfg.psicologiaapp.presentation.ui.registro.util.LimitesCaracteresRegistro
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
        val limitado = limitarTexto(nuevoCorreo, LimitesCaracteresRegistro.Psicologo.CORREO)
        _uiState.update {
            it.copy(
                correo = limitado.texto,
                errorLongitudCorreo = limitado.error,
                mensajeError = null
            )
        }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _uiState.update { it.copy(contrasena = nuevaContrasena, mensajeError = null) }
    }

    fun alCambiarNombre(nuevoNombre: String) {
        val limitado = limitarTexto(nuevoNombre, LimitesCaracteresRegistro.Psicologo.NOMBRE)
        _uiState.update {
            it.copy(
                nombre = limitado.texto,
                errorLongitudNombre = limitado.error,
                mensajeError = null
            )
        }
    }

    fun alCambiarApellidos(nuevosApellidos: String) {
        val limitado = limitarTexto(nuevosApellidos, LimitesCaracteresRegistro.Psicologo.APELLIDOS)
        _uiState.update {
            it.copy(
                apellidos = limitado.texto,
                errorLongitudApellidos = limitado.error,
                mensajeError = null
            )
        }
    }

    fun alCambiarNumeroColegiado(nuevoNumeroColegiado: String) {
        val limitado = limitarTexto(nuevoNumeroColegiado, LimitesCaracteresRegistro.Psicologo.NUMERO_COLEGIADO)
        _uiState.update {
            it.copy(
                numeroColegiado = limitado.texto,
                errorLongitudNumeroColegiado = limitado.error,
                mensajeError = null
            )
        }
    }

    fun alCambiarEspecialidadInput(texto: String) {
        val limitado = limitarTexto(texto, LimitesCaracteresRegistro.Psicologo.ESPECIALIDAD)
        _uiState.update {
            it.copy(
                especialidadInput = limitado.texto,
                errorEspecialidadInput = limitado.error,
                mensajeError = null
            )
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
            _uiState.update { it.copy(errorEspecialidadInput = "Esa especialidad ya está en la lista") }
            return
        }

        _uiState.update {
            it.copy(
                especialidades = lista + texto,
                especialidadInput = "",
                errorEspecialidadInput = null,
                mensajeError = null
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

    fun alCambiarDescripcion(nuevaDescripcion: String) {
        val limitado = limitarTexto(nuevaDescripcion, LimitesCaracteresRegistro.Psicologo.DESCRIPCION)
        _uiState.update {
            it.copy(
                descripcion = limitado.texto,
                errorLongitudDescripcion = limitado.error,
                mensajeError = null
            )
        }
    }

    fun alConsumirRegistroCompletado() {
        _uiState.update { it.copy(registroCompletado = false) }
    }

    fun registrarPsicologo() {
        val correo = uiState.value.correo.trim()
        val contrasena = uiState.value.contrasena
        val nombre = uiState.value.nombre.trim()
        val apellidos = uiState.value.apellidos.trim()
        val numeroColegiado = uiState.value.numeroColegiado.trim()
        val especialidades = uiState.value.especialidades
        val descripcion = uiState.value.descripcion.trim().ifBlank { null }

        if (
            correo.isBlank() ||
            contrasena.isBlank() ||
            nombre.isBlank() ||
            apellidos.isBlank() ||
            numeroColegiado.isBlank() ||
            especialidades.isEmpty()
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
                        nombre = nombre,
                        apellidos = apellidos,
                        fotoPerfilUrl = null,
                        numeroColegiado = numeroColegiado,
                        especialidades = especialidades,
                        descripcion = descripcion,
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

    private data class TextoLimitado(
        val texto: String,
        val error: String?
    )

    private fun limitarTexto(nuevo: String, max: Int): TextoLimitado {
        if (nuevo.length <= max) return TextoLimitado(texto = nuevo, error = null)
        return TextoLimitado(
            texto = nuevo.take(max),
            error = LimitesCaracteresRegistro.Psicologo.mensajeMaximoCaracteres(max)
        )
    }
}
