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
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.ColaDestinosNotificacion
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.DestinoPendienteNotificacion
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.SolicitarPermisoNotificacionesUnaVez
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.presentation.components.HojaMenuLateralPerfil
import dam2.tfg.psicologiaapp.presentation.ui.paciente.menuLateral.EventoNavegacionMenuLateral
import dam2.tfg.psicologiaapp.presentation.ui.paciente.menuLateral.MenuLateralPerfilViewModel
import dam2.tfg.psicologiaapp.presentation.ui.paciente.acerca.PantallaAcercaDePaciente
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.anadirTarea.PantallaAnadirTareaPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes.PantallaAjustesPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.chat.PantallaChatScreen
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.fichaPaciente.PantallaFichaPacientePsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.home.HomePsicologoViewModel
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.home.PantallaHomePsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.citas.MisCitasPsicologoScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrafoPsicologoNavegacion(
    navControllerRaiz: NavHostController,
    entradaGrafo: NavBackStackEntry,
) {
    val menuViewModel = hiltViewModel<MenuLateralPerfilViewModel>(entradaGrafo)
    val menuUi by menuViewModel.uiState.collectAsState()
    // HomePsicologoViewModel compartido entre HOME, FICHA y demás para evitar
    // re-instancias y re-cargas al navegar entre ellas.
    val homePsicologoViewModel = hiltViewModel<HomePsicologoViewModel>(entradaGrafo)
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

    SolicitarPermisoNotificacionesUnaVez()

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

    // El psicólogo gestiona varios pacientes, así que el chat necesita el id concreto.
    // La notificación de tareas no aplica al psicólogo: solo el paciente las recibe.
    // Las alertas clínicas de riesgo SÍ son exclusivas del psicólogo: abren la ficha del paciente.
    val destinoNotif by ColaDestinosNotificacion.destino.collectAsState()
    LaunchedEffect(destinoNotif) {
        when (val pendiente = destinoNotif) {
            is DestinoPendienteNotificacion.Chat -> {
                ColaDestinosNotificacion.consumir()
                if (pendiente.pacienteId > 0L) {
                    navPsicologo.navigate(RutasGrafoPsicologo.crearRutaChatPaciente(pendiente.pacienteId))
                }
            }
            is DestinoPendienteNotificacion.FichaPaciente -> {
                ColaDestinosNotificacion.consumir()
                if (pendiente.pacienteId > 0L) {
                    navPsicologo.navigate(RutasGrafoPsicologo.crearRutaFichaPaciente(pendiente.pacienteId))
                }
            }
            else -> Unit
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
                temaOscuroResuelto = temaOscuroResuelto,
                mensajeError = menuUi.mensajeError,
                cargandoFotoPerfil = menuUi.cargandoFotoPerfil,
                alPulsarFotoPerfil = abrirSelectorFoto,
                etiquetaEntradaPerfil = "Modificar perfil",
                alFijarModoTema = menuViewModel::fijarModoTema,
                alIrMisCitas = {
                    scope.launch {
                        drawerState.close()
                        navPsicologo.navigate(RutasGrafoPsicologo.MIS_CITAS)
                    }
                },
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
                    alIrAChatConPaciente = { idPaciente ->
                        navPsicologo.navigate(RutasGrafoPsicologo.crearRutaChatPaciente(idPaciente))
                    },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    viewModel = homePsicologoViewModel,
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
                    alPulsarCambiarFotoPerfil = abrirSelectorFoto,
                    cargandoFotoPerfil = menuUi.cargandoFotoPerfil,
                    mensajeErrorFotoPerfil = menuUi.mensajeError,
                )
            }

            composable(RutasGrafoPsicologo.MIS_CITAS) {
                MisCitasPsicologoScreen(
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

            composable(
                route = RutasGrafoPsicologo.CHAT_PACIENTE,
                arguments = listOf(
                    navArgument(RutasApp.ARG_PACIENTE_ID) { type = NavType.LongType },
                ),
            ) {
                PantallaChatScreen(
                    alVolver = { navPsicologo.popBackStack() },
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }
        }
    }
}
