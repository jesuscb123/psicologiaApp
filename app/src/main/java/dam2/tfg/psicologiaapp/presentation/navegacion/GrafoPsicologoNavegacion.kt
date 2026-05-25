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
import dam2.tfg.psicologiaapp.presentation.ui.paciente.menuLateral.MenuLateralPerfilViewModel
import dam2.tfg.psicologiaapp.presentation.ui.paciente.acerca.PantallaAcercaDePaciente
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.anadirTarea.PantallaAnadirTareaPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes.PantallaAjustesPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.ajustes.PantallaAjustesHubPsicologo
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

    // Observa el lifecycle del destino actual para saber si la animación de navegación
    // ha terminado. Mientras el entry no haya alcanzado RESUMED, hay una transición activa.
    val entradaActual by navPsicologo.currentBackStackEntryAsState()
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

    val manejarVolver: () -> Unit = {
        // Guard de re-entrada: si ya estamos en una transición, ignorar el click.
        // Esto evita doble-pop cuando la pantalla saliente sigue viva durante el render.
        if (!navEnTransicion) {
            val drawerEnTransicion = drawerState.currentValue != drawerState.targetValue
            if (drawerState.isOpen || drawerEnTransicion) {
                scope.launch { drawerState.close() }
            } else {
                navEnTransicion = true
                navPsicologo.popBackStack()
            }
        }
    }

    // BackHandler reactivo: usa entradaActual (Compose state) para saber si hay destino previo.
    val enHome = entradaActual?.destination?.route == RutasGrafoPsicologo.HOME
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
            navPsicologo.navigate(ruta)
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
                etiquetaEntradaPerfil = "Ajustes",
                alIrMisCitas = {
                    navegarDesdeMenu(RutasGrafoPsicologo.MIS_CITAS)
                },
                alIrAjustes = {
                    navegarDesdeMenu(RutasGrafoPsicologo.AJUSTES_HUB)
                },
            )
        },
    ) {
        NavHost(
            navController = navPsicologo,
            startDestination = RutasGrafoPsicologo.HOME,
            // Sin animaciones de transición entre destinos: eliminan la ventana de carrera
            // en la que ambas pantallas conviven en composición y permiten que toques sobre
            // la pantalla saliente disparen acciones que dejan el NavHost sin destino.
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
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
                    alVolver = manejarVolver,
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
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPsicologo.AJUSTES_HUB) {
                PantallaAjustesHubPsicologo(
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                    modoTema = menuUi.modoTema,
                    alFijarModoTema = menuViewModel::fijarModoTema,
                    alIrModificarPerfil = { navPsicologo.navigate(RutasGrafoPsicologo.AJUSTES) },
                    alIrAcercaDe = { navPsicologo.navigate(RutasGrafoPsicologo.ACERCA) },
                    alCerrarSesion = { menuViewModel.cerrarSesion() },
                )
            }

            composable(RutasGrafoPsicologo.AJUSTES) {
                PantallaAjustesPsicologo(
                    alVolver = manejarVolver,
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
                    alVolver = manejarVolver,
                    alAbrirMenuPerfil = abrirMenu,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }

            composable(RutasGrafoPsicologo.ACERCA) {
                PantallaAcercaDePaciente(
                    alVolver = manejarVolver,
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
                    alVolver = manejarVolver,
                    nombreUsuarioBarra = nombreBarra,
                    fotoPerfilUrlBarra = menuUi.fotoPerfilUrl,
                    revisionCacheFotoBarra = menuUi.revisionCacheFoto,
                )
            }
        }
    }
}
