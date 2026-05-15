package dam2.tfg.psicologiaapp.presentation.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.IniciarSesionUseCase
import dam2.tfg.psicologiaapp.auth.domain.usecase.SolicitarRestablecerContrasenaUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.RegistrarFcmTokenActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.VerificarExistenciaCorreoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class IniciarSesionViewModel @Inject constructor(
    private val iniciarSesionUseCase: IniciarSesionUseCase,
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val solicitarRestablecerContrasenaUseCase: SolicitarRestablecerContrasenaUseCase,
    private val verificarExistenciaCorreoUseCase: VerificarExistenciaCorreoUseCase,
    private val registrarFcmTokenActualUseCase: RegistrarFcmTokenActualUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(IniciarSesionUiState())
    val uiState: StateFlow<IniciarSesionUiState> = _uiState

    fun alCambiarCorreo(nuevoCorreo: String) {
        _uiState.update { it.copy(correo = nuevoCorreo, mensajeError = null) }
    }

    fun alCambiarContrasena(nuevaContrasena: String) {
        _uiState.update { it.copy(contrasena = nuevaContrasena, mensajeError = null) }
    }

    fun abrirDialogoRecuperacion() {
        _uiState.update {
            it.copy(
                mostrandoDialogoRecuperacion = true,
                correoRecuperacion = it.correoRecuperacion.ifBlank { it.correo.trim() },
                mensajeErrorRecuperacion = null
            )
        }
    }

    fun cerrarDialogoRecuperacion() {
        _uiState.update {
            it.copy(
                mostrandoDialogoRecuperacion = false,
                cargandoRecuperacion = false,
                mensajeErrorRecuperacion = null
            )
        }
    }

    fun alCambiarCorreoRecuperacion(nuevoCorreo: String) {
        _uiState.update {
            it.copy(
                correoRecuperacion = nuevoCorreo,
                mensajeErrorRecuperacion = null
            )
        }
    }

    fun alConsumirMensajeInfoRecuperacion() {
        _uiState.update { it.copy(mensajeInfoRecuperacion = null) }
    }

    fun solicitarRecuperacionContrasena() {
        val correoRecuperacion = uiState.value.correoRecuperacion.trim()

        if (correoRecuperacion.isBlank()) {
            _uiState.update { it.copy(mensajeErrorRecuperacion = "Introduce tu correo electrónico") }
            return
        }

        if (!esCorreoValido(correoRecuperacion)) {
            _uiState.update { it.copy(mensajeErrorRecuperacion = "El formato del correo no es válido") }
            return
        }

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    cargandoRecuperacion = true,
                    mensajeErrorRecuperacion = null,
                    mensajeInfoRecuperacion = null
                )
            }

            val resultadoVerificacion = verificarExistenciaCorreoUseCase(email = correoRecuperacion)
            val existeCorreo = resultadoVerificacion.getOrElse {
                _uiState.update { estadoActual ->
                    estadoActual.copy(
                        cargandoRecuperacion = false,
                        mensajeErrorRecuperacion = null,
                        mensajeInfoRecuperacion = MENSAJE_ERROR_VERIFICAR_CORREO_BACKEND
                    )
                }
                return@launch
            }

            if (!existeCorreo) {
                // Mensaje neutro: no se revela si el correo existe o no (anti-enumeración).
                _uiState.update { estadoActual ->
                    estadoActual.copy(
                        cargandoRecuperacion = false,
                        mensajeErrorRecuperacion = null,
                        mensajeInfoRecuperacion = MENSAJE_NEUTRO_RECUPERACION
                    )
                }
                return@launch
            }

            val resultado = solicitarRestablecerContrasenaUseCase(correo = correoRecuperacion)
            resultado.fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(
                            mostrandoDialogoRecuperacion = false,
                            cargandoRecuperacion = false,
                            correoRecuperacion = "",
                            mensajeErrorRecuperacion = null,
                            mensajeInfoRecuperacion = MENSAJE_EXITO_RECUPERACION
                        )
                    }
                },
                onFailure = { error ->
                    val esErrorRed = error is FirebaseNetworkException || error is IOException
                    _uiState.update { estadoActual ->
                        if (esErrorRed) {
                            estadoActual.copy(
                                cargandoRecuperacion = false,
                                mensajeErrorRecuperacion = null,
                                mensajeInfoRecuperacion = MENSAJE_ERROR_RED_RECUPERACION
                            )
                        } else if (esErrorUsuarioNoEncontrado(error)) {
                            estadoActual.copy(
                                cargandoRecuperacion = false,
                                mensajeErrorRecuperacion = null,
                                mensajeInfoRecuperacion = MENSAJE_ERROR_USUARIO_NO_EXISTE
                            )
                        } else {
                            estadoActual.copy(
                                cargandoRecuperacion = false,
                                mensajeErrorRecuperacion = null,
                                mensajeInfoRecuperacion = MENSAJE_ERROR_DESCONOCIDO_RECUPERACION
                            )
                        }
                    }
                }
            )
        }
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
                                RolUsuario.PACIENTE -> {
                                    registrarTokenFcmEnSegundoPlano()
                                    _uiState.update {
                                        it.copy(cargando = false, eventoNavegacion = EventoNavegacionIniciarSesion.IrAHomePaciente)
                                    }
                                }
                                RolUsuario.PSICOLOGO -> {
                                    registrarTokenFcmEnSegundoPlano()
                                    _uiState.update {
                                        it.copy(cargando = false, eventoNavegacion = EventoNavegacionIniciarSesion.IrAHomePsicologo)
                                    }
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
                    val esErrorRed = error is FirebaseNetworkException || error is IOException
                    _uiState.update {
                        it.copy(
                            cargando = false,
                            mensajeError = if (esErrorRed) MENSAJE_ERROR_RED_LOGIN
                                          else MENSAJE_ERROR_CREDENCIALES_LOGIN
                        )
                    }
                }
            )
        }
    }

    /**
     * Registra el token FCM en el backend sin bloquear la navegación post-login: si falla
     * (red, backend caído, etc.) el usuario sigue avanzando y se reintentará en el próximo
     * onNewToken o login.
     */
    private fun registrarTokenFcmEnSegundoPlano() {
        viewModelScope.launch {
            registrarFcmTokenActualUseCase()
        }
    }

    private fun esCorreoValido(correo: String): Boolean {
        val patronCorreo = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")
        return patronCorreo.matches(correo)
    }

    private fun esErrorUsuarioNoEncontrado(error: Throwable): Boolean {
        if (error is FirebaseAuthInvalidUserException) return true
        val mensaje = error.message?.lowercase().orEmpty()
        return mensaje.contains("user-not-found") ||
            mensaje.contains("no user record found") ||
            mensaje.contains("usuario no encontrado")
    }

    companion object {
        private const val MENSAJE_EXITO_RECUPERACION =
            "Correo enviado correctamente. Revisa tu bandeja y también spam."
        /** Mensaje neutro: no revela si el correo está registrado (anti-enumeración). */
        private const val MENSAJE_NEUTRO_RECUPERACION =
            "Si el correo está registrado, recibirás un enlace de recuperación."
        private const val MENSAJE_ERROR_RED_RECUPERACION =
            "No se pudo enviar el correo por un problema de red. Inténtalo de nuevo."
        private const val MENSAJE_ERROR_DESCONOCIDO_RECUPERACION =
            "No se pudo enviar el correo de restablecimiento. Inténtalo de nuevo."
        private const val MENSAJE_ERROR_VERIFICAR_CORREO_BACKEND =
            "No se pudo verificar el correo en el servidor. Inténtalo de nuevo."
        private const val MENSAJE_ERROR_CREDENCIALES_LOGIN =
            "Correo o contraseña incorrectos."
        private const val MENSAJE_ERROR_RED_LOGIN =
            "No se pudo iniciar sesión por un problema de red. Inténtalo de nuevo."
    }
}

