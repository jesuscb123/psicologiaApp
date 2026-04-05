package dam2.tfg.psicologiaapp.presentation.ui.paciente

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dam2.tfg.psicologiaapp.auth.domain.usecase.CerrarSesionUseCase
import dam2.tfg.psicologiaapp.paciente.domain.model.PacientePerfil
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.EstablecerModoTemaUseCase
import dam2.tfg.psicologiaapp.preferencias.domain.usecase.ObservarModoTemaUseCase
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario
import dam2.tfg.psicologiaapp.usuario.domain.usecase.GetPerfilActualUseCase
import dam2.tfg.psicologiaapp.usuario.domain.usecase.SincronizarFotoPerfilUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@HiltViewModel
class MenuLateralPerfilViewModel @Inject constructor(
    private val getPerfilActualUseCase: GetPerfilActualUseCase,
    private val observarModoTemaUseCase: ObservarModoTemaUseCase,
    private val establecerModoTemaUseCase: EstablecerModoTemaUseCase,
    private val cerrarSesionUseCase: CerrarSesionUseCase,
    private val sincronizarFotoPerfilUseCase: SincronizarFotoPerfilUseCase,
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
        recargarPerfil()
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
                            nombreUsuario = perfil.nombreUsuario,
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

    fun recargarPerfil() {
        viewModelScope.launch {
            _uiState.update { it.copy(cargandoPerfil = true, mensajeError = null) }
            getPerfilActualUseCase().fold(
                onSuccess = { perfil ->
                    if (perfil.rol != RolUsuario.PACIENTE) {
                        _uiState.update {
                            it.copy(
                                cargandoPerfil = false,
                                mensajeError = "Perfil no válido",
                            )
                        }
                        return@launch
                    }
                    val paciente = perfil as? PacientePerfil
                    if (paciente == null) {
                        _uiState.update {
                            it.copy(
                                cargandoPerfil = false,
                                mensajeError = "No se pudo cargar el perfil",
                            )
                        }
                        return@launch
                    }
                    _uiState.update { prev ->
                        val nuevaUrl = paciente.fotoPerfilUrl
                        val revision = if (nuevaUrl != prev.fotoPerfilUrl) {
                            prev.revisionCacheFoto + 1L
                        } else {
                            prev.revisionCacheFoto
                        }
                        prev.copy(
                            cargandoPerfil = false,
                            nombreUsuario = paciente.nombreUsuario,
                            fotoPerfilUrl = nuevaUrl,
                            revisionCacheFoto = revision,
                            mensajeError = null,
                        )
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
            cerrarSesionUseCase().fold(
                onSuccess = {
                    _uiState.update {
                        it.copy(eventoNavegacion = EventoNavegacionMenuLateral.SesionCerrada)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(
                            mensajeError = error.message ?: "No se pudo cerrar la sesión",
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
