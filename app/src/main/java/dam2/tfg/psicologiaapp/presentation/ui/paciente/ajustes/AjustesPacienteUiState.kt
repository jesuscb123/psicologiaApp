package dam2.tfg.psicologiaapp.presentation.ui.paciente.ajustes

import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp

data class AjustesPacienteUiState(
    val modoTema: ModoTemaApp = ModoTemaApp.SeguirSistema,
)
