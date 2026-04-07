package dam2.tfg.psicologiaapp.presentation.ui.splash

import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario

data class SesionArranqueUiState(
    val destinoResuelto: DestinoSesion? = null,
    val forzarIrALogin: Boolean = false,
)

sealed interface DestinoSesion {
    data object IniciarSesion : DestinoSesion
    data class Grafo(val rol: RolUsuario) : DestinoSesion
}

