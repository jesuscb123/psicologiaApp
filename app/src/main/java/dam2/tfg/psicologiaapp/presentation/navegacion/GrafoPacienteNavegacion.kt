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
import dam2.tfg.psicologiaapp.notificaciones.presentation.ColaDestinosNotificacion
import dam2.tfg.psicologiaapp.notificaciones.presentation.DestinoPendienteNotificacion
import dam2.tfg.psicologiaapp.notificaciones.presentation.SolicitarPermisoNotificacionesUnaVez
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.presentation.components.HojaMenuLateralPerfil
import dam2.tfg.psicologiaapp.presentation.ui.paciente.EventoNavegacionMenuLateral
import dam2.tfg.psicologiaapp.presentation.ui.paciente.HomePacienteViewModel
import dam2.tfg.psicologiaapp.presentation.ui.paciente.MenuLateralPerfilViewModel
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaAcercaDePaciente
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaAjustesPaciente
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaAnadirNota
import dam2.tfg.psicologiaapp.presentation.ui.paciente.citas.CitasScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.citas.CitasMenuScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.citas.MisCitasPacienteScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.NotasPacienteScreen
import dam2.tfg.psicologiaapp.presentation.ui.chat.PantallaChatScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaHomePaciente
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaPerfilPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.paciente.TareasPacienteScreen
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrafoPacienteNavegacion(
    navControllerRaiz: NavHostController,
    entradaGrafo: NavBackStackEntry,
) {
    val menuViewModel = hiltViewModel<MenuLateralPerfilViewModel>(entradaGrafo)
    val menuUi by menuViewModel.uiState.collectAsState()
    // HomePacienteViewModel compartido entre HOME, NOTAS y TAREAS para evitar
    // re-instancias y re-cargas al navegar entre ellas.
    val homePacienteViewModel = hiltViewModel<HomePacienteViewModel>(entradaGrafo)
    val navPaciente = rememberNavController()
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

    // Consume el destino dejado por una notificación al entrar al grafo paciente.
    // El paciente solo tiene un chat (con su psicólogo), así que no necesitamos los ids.
    // Los destinos clínicos (FichaPaciente) son exclusivos del grafo del psicólogo; si llegan
    // aquí por error los descartamos para no dejar la cola bloqueada.
    val destinoNotif by ColaDestinosNotificacion.destino.collectAsState()
    LaunchedEffect(destinoNotif) {
        when (destinoNotif) {
            is DestinoPendienteNotificacion.Chat -> {
                ColaDestinosNotificacion.consumir()
                navPaciente.navigate(RutasGrafoPaciente.CHAT_PSICOLOGO)
            }
            is DestinoPendienteNotificacion.TareasPaciente -> {
                ColaDestinosNotificacion.consumir()
                navPaciente.navigate(RutasGrafoPaciente.TAREAS)
            }
            is DestinoPendienteNotificacion.FichaPaciente -> {
                ColaDestinosNotificacion.consumir()
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
                alIrAgendarCita = {
                    scope.launch {
                        drawerState.close()
                        navPaciente.navigate(RutasGrafoPaciente.AGENDAR_CITA)
                    }
                },
                alIrMisCitas = {
                    scope.launch {
                        drawerState.close()
                        navPaciente.navigate(RutasGrafoPaciente.MIS_CITAS)
                    }
                },
                alIrAjustes = {
                    scope.launch {
                        drawerState.close()
                        navPaciente.navigate(RutasGrafoPaciente.AJUSTES)
                    }
                },
                alAcercaDe = {
                    scope.launch {
                        drawerState.close()
                        navPaciente.navigate(RutasGrafoPaciente.ACERCA)
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
            navController = navPaciente,
            startDestination = RutasGrafoPaciente.HOME,
        ) {
            composable(RutasGrafoPaciente.HOME) {
                PantallaHomePaciente(
                    alIrAPerfilPsicologo = { id ->
                        navPaciente.navigate(RutasGrafoPaciente.crearRutaPerfilPsicologo(id))
                    },
                    alIrANotas = { navPaciente.navigate(RutasGrafoPaciente.NOTAS) },
                    alIrATareas = { navPaciente.navigate(RutasGrafoPaciente.TAREAS) },
                    alIrACitas = { navPaciente.navigate(RutasGrafoPaciente.CITAS_MENU) },
                    alIrAAjustes = { navPaciente.navigate(RutasGrafoPaciente.AJUSTES) },
                    alIrAChat = { navPaciente.navigate(RutasGrafoPaciente.CHAT_PSICOLOGO) },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    viewModel = homePacienteViewModel,
                )
            }

            composable(
                route = RutasGrafoPaciente.PERFIL_PSICOLOGO,
                arguments = listOf(
                    navArgument(RutasApp.ARG_PSICOLOGO_ID) { type = NavType.StringType },
                ),
            ) { entrada ->
                val psicologoId = entrada.arguments?.getString(RutasApp.ARG_PSICOLOGO_ID).orEmpty()
                PantallaPerfilPsicologo(
                    psicologoId = psicologoId,
                    alAsignacionCompletada = {
                        navPaciente.navigate(RutasGrafoPaciente.HOME) {
                            popUpTo(RutasGrafoPaciente.HOME) { inclusive = true }
                        }
                    },
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.NOTAS) {
                NotasPacienteScreen(
                    alVolver = { navPaciente.popBackStack() },
                    alIrAAnadirNota = { navPaciente.navigate(RutasGrafoPaciente.ANADIR_NOTA) },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    viewModel = homePacienteViewModel,
                )
            }

            composable(RutasGrafoPaciente.ANADIR_NOTA) {
                PantallaAnadirNota(
                    alNotaGuardada = { navPaciente.popBackStack() },
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.TAREAS) {
                TareasPacienteScreen(
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    viewModel = homePacienteViewModel,
                )
            }

            composable(RutasGrafoPaciente.CITAS_MENU) {
                CitasMenuScreen(
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    alIrAgendar = { navPaciente.navigate(RutasGrafoPaciente.AGENDAR_CITA) },
                    alIrMisCitas = { navPaciente.navigate(RutasGrafoPaciente.MIS_CITAS) },
                )
            }

            composable(RutasGrafoPaciente.AGENDAR_CITA) {
                CitasScreen(
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.MIS_CITAS) {
                MisCitasPacienteScreen(
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.AJUSTES) {
                PantallaAjustesPaciente(
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.ACERCA) {
                PantallaAcercaDePaciente(
                    alVolver = { navPaciente.popBackStack() },
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.CHAT_PSICOLOGO) {
                PantallaChatScreen(
                    alVolver = { navPaciente.popBackStack() },
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }
        }
    }
}
