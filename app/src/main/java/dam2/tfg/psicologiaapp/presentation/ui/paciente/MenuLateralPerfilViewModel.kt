package dam2.tfg.psicologiaapp.presentation.ui.paciente

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.CerrarSesionUseCase
import dam2.tfg.psicologiaapp.notificaciones.domain.usecase.DarDeBajaFcmTokenUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.LimpiarTodosDatosLocalesUseCase
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.EstablecerModoTemaUseCase
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.ObservarModoTemaUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.model.nombreCompleto
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.ObservarPerfilCacheadoUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarFotoPerfilUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class MenuLateralPerfilViewModel @Inject constructor(
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val observarPerfilCacheadoUseCase: ObservarPerfilCacheadoUseCase,
    private val observarModoTemaUseCase: ObservarModoTemaUseCase,
    private val establecerModoTemaUseCase: EstablecerModoTemaUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
    private val sincronizarFotoPerfilUseCase: SincronizarFotoPerfilUseCase,
    private val darDeBajaFcmTokenUseCase: DarDeBajaFcmTokenUseCase,
    private val limpiarTodosDatosLocalesUseCase: LimpiarTodosDatosLocalesUseCase,
    @ApplicationContext private val application: Context,
) : ViewModel() {

    private companion object {
        const val LIMITE_BYTES_IMAGEN_PERFIL = 5 * 1024 * 1024
    }

    private val _uiState = MutableStateFlow(MenuLateralPerfilUiState())
    val uiState: StateFlow<MenuLateralPerfilUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observarModoTemaUseCase().collect { modo ->
                _uiState.update { it.copy(modoTema = modo) }
            }
        }
        viewModelScope.launch {
            observarPerfilCacheadoUseCase().collectLatest { perfil ->
                if (perfil != null) {
                    val nombre = listOf(perfil.nombre, perfil.apellidos)
                        .filter { it.isNotBlank() }.joinToString(" ")
                    _uiState.update { prev ->
                        val nuevaUrl = perfil.fotoPerfilUrl
                        val revision = if (nuevaUrl != prev.fotoPerfilUrl) {
                            prev.revisionCacheFoto + 1L
                        } else {
                            prev.revisionCacheFoto
                        }
                        prev.copy(
                            cargandoPerfil = false,
                            nombreUsuario = nombre,
                            fotoPerfilUrl = nuevaUrl,
                            revisionCacheFoto = revision,
                        )
                    }
                }
            }
        }
        // Background refresh: update cache from network without blocking UI.
        viewModelScope.launch {
            recargarPerfilEnBackground()
        }
    }

    fun procesarUriNuevaFoto(uri: Uri) {
        viewModelScope.launch {
            if (_uiState.value.cargandoFotoPerfil) return@launch
            _uiState.update { it.copy(cargandoFotoPerfil = true, mensajeError = null) }
            val lectura = withContext(Dispatchers.IO) {
                runCatching {
                    application.contentResolver.openInputStream(uri)?.use { flujo ->
                        flujo.readBytes()
                    } ?: error("No se pudo leer la imagen seleccionada")
                }
            }
            val bytes = lectura.getOrElse { error ->
                _uiState.update {
                    it.copy(
                        cargandoFotoPerfil = false,
                        mensajeError = error.message ?: "No se pudo leer la imagen",
                    )
                }
                return@launch
            }
            if (bytes.isEmpty()) {
                _uiState.update {
                    it.copy(cargandoFotoPerfil = false, mensajeError = "El fichero está vacío")
                }
                return@launch
            }
            if (bytes.size > LIMITE_BYTES_IMAGEN_PERFIL) {
                _uiState.update {
                    it.copy(
                        cargandoFotoPerfil = false,
                        mensajeError = "La imagen supera el tamaño máximo (5 MB)",
                    )
                }
                return@launch
            }
            val tipoMime = application.contentResolver.getType(uri)
            sincronizarFotoPerfilUseCase(bytes, tipoMime).fold(
                onSuccess = { perfil ->
                    _uiState.update {
                        it.copy(
                            cargandoFotoPerfil = false,
                            nombreUsuario = perfil.nombreCompleto(),
                            fotoPerfilUrl = perfil.fotoPerfilUrl,
                            revisionCacheFoto = it.revisionCacheFoto + 1L,
                            mensajeError = null,
                        )
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            cargandoFotoPerfil = false,
                            mensajeError = error.message ?: "No se pudo actualizar la foto",
                        )
                    }
                },
            )
        }
    }

    private suspend fun recargarPerfilEnBackground() {
        getPerfilActualUseCase().fold(
            onSuccess = { perfil ->
                when (perfil.rol) {
                    RolUsuario.PACIENTE, RolUsuario.PSICOLOGO -> {
                        _uiState.update { prev ->
                            val nuevaUrl = perfil.fotoPerfilUrl
                            val revision = if (nuevaUrl != prev.fotoPerfilUrl) {
                                prev.revisionCacheFoto + 1L
                            } else {
                                prev.revisionCacheFoto
                            }
                            prev.copy(
                                cargandoPerfil = false,
                                nombreUsuario = perfil.nombreCompleto(),
                                fotoPerfilUrl = nuevaUrl,
                                revisionCacheFoto = revision,
                                mensajeError = null,
                            )
                        }
                    }
                    else -> {
                        _uiState.update {
                            it.copy(
                                cargandoPerfil = false,
                                mensajeError = "Perfil no válido",
                            )
                        }
                    }
                }
            },
            onFailure = { error ->
                _uiState.update {
                    it.copy(
                        cargandoPerfil = false,
                        mensajeError = error.message ?: "No se pudo cargar el perfil",
                    )
                }
            },
        )
    }

    fun recargarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoPerfil = _uiState.value.nombreUsuario.isEmpty(), mensajeError = null) }
            recargarPerfilEnBackground()
        }
    }

    fun fijarModoTema(modo: ModoTemaApp) {
        viewModelScope.launch {
            establecerModoTemaUseCase(modo).onFailure { error ->
                _uiState.update {
                    it.copy(mensajeError = error.message ?: "No se pudo guardar el tema")
                }
            }
        }
    }

    fun cerrarSesion() {
        viewModelScope.launch {
            // El token debe darse de baja con la sesión todavía válida (Authorization Bearer).
            // Si falla no abortamos: lo importante es que la sesión local quede cerrada.
            darDeBajaFcmTokenUseCase()

            // Limpiar todos los datos locales (Room + DataStore) antes de cerrar la sesión
            // para evitar que un segundo usuario acceda al historial clínico del anterior.
            runCatching { limpiarTodosDatosLocalesUseCase() }

            cerrarSesionUseCase().fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(eventoNavegacion = EventoNavegacionMenuLateral.SesionCerrada)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            mensajeError = "No se pudo cerrar la sesión",
                        )
                    }
                },
            )
        }
    }

    fun alConsumirEventoNavegacion() {
        _uiState.update { it.copy(eventoNavegacion = null) }
    }
}
