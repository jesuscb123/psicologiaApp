package dam2.tfg.psicologiaapp.presentation.navegacion

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.ColaDestinosNotificacion
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.DestinoPendienteNotificacion
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.SolicitarPermisoNotificacionesUnaVez
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.presentation.components.HojaMenuLateralPerfil
import dam2.tfg.psicologiaapp.presentation.ui.paciente.menuLateral.EventoNavegacionMenuLateral
import dam2.tfg.psicologiaapp.presentation.ui.paciente.home.HomePacienteViewModel
import dam2.tfg.psicologiaapp.presentation.ui.paciente.menuLateral.MenuLateralPerfilViewModel
import dam2.tfg.psicologiaapp.presentation.ui.paciente.acerca.PantallaAcercaDePaciente
import dam2.tfg.psicologiaapp.presentation.ui.paciente.ajustes.PantallaAjustesPaciente
import dam2.tfg.psicologiaapp.presentation.ui.paciente.anadirNota.PantallaAnadirNota
import dam2.tfg.psicologiaapp.presentation.ui.paciente.citas.CitasScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.citas.CitasMenuScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.citas.MisCitasPacienteScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.home.NotasPacienteScreen
import dam2.tfg.psicologiaapp.presentation.ui.chat.PantallaChatScreen
import dam2.tfg.psicologiaapp.presentation.ui.paciente.home.PantallaHomePaciente
import dam2.tfg.psicologiaapp.presentation.ui.paciente.perfilPsicologo.PantallaPerfilPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.paciente.home.TareasPacienteScreen
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

    // Observa el lifecycle del destino actual para saber si la animación de navegación
    // ha terminado. Mientras el entry no haya alcanzado RESUMED, hay una transición activa.
    val entradaActual by navPaciente.currentBackStackEntryAsState()
    var navEnTransicion by remember { mutableStateOf(false) }
    DisposableEffect(entradaActual) {
        val lifecycle = entradaActual?.lifecycle
        if (lifecycle == null) {
            navEnTransicion = false
            return@DisposableEffect onDispose {}
        }
        navEnTransicion = !lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        val observer = LifecycleEventObserver { _, _ ->
            navEnTransicion = !lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)
        }
        lifecycle.addObserver(observer)
        onDispose { lifecycle.removeObserver(observer) }
    }
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

    val manejarVolver: () -> Unit = {
        // Guard de re-entrada: si ya estamos en una transición, ignorar el click.
        // Esto evita doble-pop cuando la pantalla saliente sigue viva durante el render.
        if (!navEnTransicion) {
            val drawerEnTransicion = drawerState.currentValue != drawerState.targetValue
            if (drawerState.isOpen || drawerEnTransicion) {
                scope.launch { drawerState.close() }
            } else {
                navEnTransicion = true
                navPaciente.popBackStack()
            }
        }
    }

    // BackHandler reactivo: usa entradaActual (Compose state) para saber si hay destino previo.
    val enHome = entradaActual?.destination?.route == RutasGrafoPaciente.HOME
    val puedeManejarBack = !enHome || drawerState.isOpen || navEnTransicion
    BackHandler(enabled = puedeManejarBack) {
        manejarVolver()
    }

    val abrirMenu: () -> Unit = {
        val drawerEnTransicion = drawerState.currentValue != drawerState.targetValue
        if (drawerState.isClosed && !drawerEnTransicion && !navEnTransicion) {
            scope.launch { drawerState.open() }
        }
    }

    val navegarDesdeMenu: (String) -> Unit = { ruta ->
        scope.launch {
            val drawerEnTransicion = drawerState.currentValue != drawerState.targetValue
            if (drawerState.isOpen || drawerEnTransicion) {
                drawerState.close()
            }
            navPaciente.navigate(ruta)
        }
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
        gesturesEnabled = !navEnTransicion,
        drawerContent = {
            HojaMenuLateralPerfil(
                nombreUsuario = nombreBarra,
                fotoPerfilUrl = menuUi.fotoPerfilUrl,
                revisionCacheFoto = menuUi.revisionCacheFoto,
                temaOscuroResuelto = temaOscuroResuelto,
                mensajeError = menuUi.mensajeError,
                cargandoFotoPerfil = menuUi.cargandoFotoPerfil,
                alPulsarFotoPerfil = abrirSelectorFoto,
                alIrAgendarCita = {
                    navegarDesdeMenu(RutasGrafoPaciente.AGENDAR_CITA)
                },
                alIrMisCitas = {
                    navegarDesdeMenu(RutasGrafoPaciente.MIS_CITAS)
                },
                alIrAjustes = {
                    navegarDesdeMenu(RutasGrafoPaciente.AJUSTES)
                },
            )
        },
    ) {
        NavHost(
            navController = navPaciente,
            startDestination = RutasGrafoPaciente.HOME,
            // Sin animaciones de transición entre destinos: eliminan la ventana de carrera
            // en la que ambas pantallas conviven en composición y permiten que toques sobre
            // la pantalla saliente disparen acciones que dejan el NavHost sin destino.
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
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
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.NOTAS) {
                NotasPacienteScreen(
                    alVolver = manejarVolver,
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
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.TAREAS) {
                TareasPacienteScreen(
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    viewModel = homePacienteViewModel,
                )
            }

            composable(RutasGrafoPaciente.CITAS_MENU) {
                CitasMenuScreen(
                    alVolver = manejarVolver,
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
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.MIS_CITAS) {
                MisCitasPacienteScreen(
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.AJUSTES) {
                PantallaAjustesPaciente(
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    alIrAcercaDe = { navPaciente.navigate(RutasGrafoPaciente.ACERCA) },
                    alCerrarSesion = { menuViewModel.cerrarSesion() },
                )
            }

            composable(RutasGrafoPaciente.ACERCA) {
                PantallaAcercaDePaciente(
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPaciente.CHAT_PSICOLOGO) {
                PantallaChatScreen(
                    alVolver = manejarVolver,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }
        }
    }
}
