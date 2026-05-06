package dam2.tfg.psicologiaapp.presentation.ui.splash

import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.CerrarSesionUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.RegistrarFcmTokenActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GuardarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.LimpiarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObtenerPerfilCacheadoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SesionArranqueViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val obtenerPerfilCacheadoUseCase: ObtenerPerfilCacheadoUseCase,
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val guardarPerfilCacheadoUseCase: GuardarPerfilCacheadoUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
    private val limpiarPerfilCacheadoUseCase: LimpiarPerfilCacheadoUseCase,
    private val registrarFcmTokenActualUseCase: RegistrarFcmTokenActualUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SesionArranqueUiState())
    val uiState: StateFlow<SesionArranqueUiState> = _uiState.asStateFlow()

    init {
        resolverDestinoInicialYValidar()
    }

    private fun resolverDestinoInicialYValidar() {
        val usuarioFirebase = firebaseAuth.currentUser
        if (usuarioFirebase == null) {
            _uiState.update { it.copy(destinoResuelto = DestinoSesion.IniciarSesion) }
            return
        }

        val firebaseUid = usuarioFirebase.uid

        viewModelScope.launch {
            val cache = obtenerPerfilCacheadoUseCase(firebaseUid)
            val rolCache = cache?.rol

            val destino = when (rolCache) {
                RolUsuario.PACIENTE, RolUsuario.PSICOLOGO -> DestinoSesion.Grafo(rol = rolCache)
                else -> null
            }

            if (destino != null) {
                _uiState.update { it.copy(destinoResuelto = destino) }
                validarPerfilEnBackground()
                registrarTokenFcmEnSegundoPlano()
                return@launch
            }

            // Sin caché utilizable: resolvemos por red antes de navegar.
            getPerfilActualUseCase().fold(
                onSuccess = { perfil ->
                    guardarPerfilCacheadoUseCase(perfil)
                    when (perfil.rol) {
                        RolUsuario.PACIENTE, RolUsuario.PSICOLOGO -> {
                            _uiState.update { it.copy(destinoResuelto = DestinoSesion.Grafo(rol = perfil.rol)) }
                            registrarTokenFcmEnSegundoPlano()
                        }
                        else ->
                            _uiState.update { it.copy(destinoResuelto = DestinoSesion.IniciarSesion) }
                    }
                },
                onFailure = {
                    _uiState.update { it.copy(destinoResuelto = DestinoSesion.IniciarSesion) }
                },
            )
        }
    }

    /**
     * Reasegura el registro del token FCM siempre que la app arranca con sesión válida.
     * Es idempotente: el backend deduplica por token y por instalación, así que lo lanzamos
     * "fire and forget" para cubrir el caso en que el token rotó con la app cerrada.
     */
    private fun registrarTokenFcmEnSegundoPlano() {
        viewModelScope.launch {
            registrarFcmTokenActualUseCase()
        }
    }

    private fun validarPerfilEnBackground() {
        viewModelScope.launch {
            val resultado = getPerfilActualUseCase()
            resultado.fold(
                onSuccess = { perfil ->
                    guardarPerfilCacheadoUseCase(perfil)
                },
                onFailure = {
                    cerrarSesionUseCase()
                    limpiarPerfilCacheadoUseCase()
                    _uiState.update { it.copy(forzarIrALogin = true) }
                },
            )
        }
    }

    fun alConsumirForzarLogin() {
        _uiState.update { it.copy(forzarIrALogin = false) }
    }
}

