package dam2.tfg.psicologiaapp.presentation.ui.paciente

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp

data class MenuLateralPerfilUiState(
    val nombreUsuario: String = "",
    val fotoPerfilUrl: String? = null,
    /** Incrementa al cambiar la foto para que Coil no muestre una imagen en caché obsoleta. */
    val revisionCacheFoto: Long = 0L,
    val modoTema: ModoTemaApp = ModoTemaApp.SeguirSistema,
    val cargandoPerfil: Boolean = false,
    val cargandoFotoPerfil: Boolean = false,
    val mensajeError: String? = null,
    val eventoNavegacion: EventoNavegacionMenuLateral? = null,
)

sealed class EventoNavegacionMenuLateral {
    data object SesionCerrada : EventoNavegacionMenuLateral()
}
