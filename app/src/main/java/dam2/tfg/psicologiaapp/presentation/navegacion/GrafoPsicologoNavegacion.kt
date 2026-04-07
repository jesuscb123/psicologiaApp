package dam2.tfg.psicologiaapp.presentation.navegacion

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.presentation.components.HojaMenuLateralPerfil
import dam2.tfg.psicologiaapp.presentation.ui.paciente.EventoNavegacionMenuLateral
import dam2.tfg.psicologiaapp.presentation.ui.paciente.MenuLateralPerfilViewModel
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaAcercaDePaciente
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.PantallaAnadirTareaPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.PantallaAjustesPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.PantallaFichaPacientePsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.PantallaHomePsicologo
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrafoPsicologoNavegacion(
    navControllerRaiz: NavHostController,
    entradaGrafo: NavBackStackEntry,
) {
    val menuViewModel = hiltViewModel<MenuLateralPerfilViewModel>(entradaGrafo)
    val menuUi by menuViewModel.uiState.collectAsState()
    val navPsicologo = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val sistemaEnOscuro = isSystemInDarkTheme()
    val temaOscuroResuelto = when (menuUi.modoTema) {
        ModoTemaApp.SeguirSistema -> sistemaEnOscuro
        ModoTemaApp.Claro -> false
        ModoTemaApp.Oscuro -> true
    }

    val nombreBarra = menuUi.nombreUsuario.ifBlank { "Usuario" }

    LaunchedEffect(menuUi.eventoNavegacion) {
        when (menuUi.eventoNavegacion) {
            EventoNavegacionMenuLateral.SesionCerrada -> {
                menuViewModel.alConsumirEventoNavegacion()
                navControllerRaiz.navigate(RutasApp.INICIAR_SESION) {
                    popUpTo(0) { inclusive = true }
                }
            }
            null -> Unit
        }
    }

    val abrirMenu: () -> Unit = {
        scope.launch { drawerState.open() }
    }

    val lanzadorElegirFoto = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
    ) { uri ->
        if (uri != null) {
            menuViewModel.procesarUriNuevaFoto(uri)
        }
    }

    val abrirSelectorFoto: () -> Unit = {
        if (!menuUi.cargandoFotoPerfil) {
            lanzadorElegirFoto.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
            )
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HojaMenuLateralPerfil(
                nombreUsuario = nombreBarra,
                fotoPerfilUrl = menuUi.fotoPerfilUrl,
                revisionCacheFoto = menuUi.revisionCacheFoto,
                modoTema = menuUi.modoTema,
                temaOscuroResuelto = temaOscuroResuelto,
                mensajeError = menuUi.mensajeError,
                cargandoFotoPerfil = menuUi.cargandoFotoPerfil,
                alPulsarFotoPerfil = abrirSelectorFoto,
                alFijarModoTema = menuViewModel::fijarModoTema,
                alIrAjustes = {
                    scope.launch {
                        drawerState.close()
                        navPsicologo.navigate(RutasGrafoPsicologo.AJUSTES)
                    }
                },
                alAcercaDe = {
                    scope.launch {
                        drawerState.close()
                        navPsicologo.navigate(RutasGrafoPsicologo.ACERCA)
                    }
                },
                alCerrarSesion = {
                    scope.launch {
                        drawerState.close()
                        menuViewModel.cerrarSesion()
                    }
                },
            )
        },
    ) {
        NavHost(
            navController = navPsicologo,
            startDestination = RutasGrafoPsicologo.HOME,
        ) {
            composable(RutasGrafoPsicologo.HOME) {
                PantallaHomePsicologo(
                    alIrAFichaPaciente = { idPaciente ->
                        navPsicologo.navigate(RutasGrafoPsicologo.crearRutaFichaPaciente(idPaciente))
                    },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(
                route = RutasGrafoPsicologo.FICHA_PACIENTE,
                arguments = listOf(
                    navArgument(RutasApp.ARG_PACIENTE_ID) { type = NavType.LongType },
                ),
            ) { entrada ->
                val pacienteId = entrada.arguments?.getLong(RutasApp.ARG_PACIENTE_ID) ?: 0L
                PantallaFichaPacientePsicologo(
                    alVolver = { navPsicologo.popBackStack() },
                    alIrAnadirTarea = {
                        navPsicologo.navigate(RutasGrafoPsicologo.crearRutaAnadirTarea(pacienteId))
                    },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(
                route = RutasGrafoPsicologo.ANADIR_TAREA,
                arguments = listOf(
                    navArgument(RutasApp.ARG_PACIENTE_ID) { type = NavType.LongType },
                ),
            ) {
                PantallaAnadirTareaPsicologo(
                    alTareaGuardada = { navPsicologo.popBackStack() },
                    alVolver = { navPsicologo.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPsicologo.AJUSTES) {
                PantallaAjustesPsicologo(
                    alVolver = { navPsicologo.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPsicologo.ACERCA) {
                PantallaAcercaDePaciente(
                    alVolver = { navPsicologo.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }
        }
    }
}
