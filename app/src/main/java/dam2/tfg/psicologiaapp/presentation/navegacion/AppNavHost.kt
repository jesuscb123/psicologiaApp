package dam2.tfg.psicologiaapp.presentation.navegacion

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import dam2.tfg.psicologiaapp.presentation.ui.inicio.PantallaIniciarSesion
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaRegistroPaciente
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaRegistroPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaSeleccionRolRegistro
import dam2.tfg.psicologiaapp.presentation.ui.splash.DestinoSesion
import dam2.tfg.psicologiaapp.presentation.ui.splash.PantallaSplash
import dam2.tfg.psicologiaapp.presentation.ui.splash.SesionArranqueViewModel
import dam2.tfg.psicologiaapp.usuario.domain.model.RolUsuario

@Composable
fun AppNavHost() {
    val navController = rememberNavController()
    val sesionArranqueViewModel: SesionArranqueViewModel = hiltViewModel()
    val sesionUi by sesionArranqueViewModel.uiState.collectAsState()
    val entradaActual by navController.currentBackStackEntryAsState()
    val rutaActual = entradaActual?.destination?.route
    val mostrarTopBarCompartida = rutaActual != RutasApp.INICIAR_SESION

    LaunchedEffect(sesionUi.forzarIrALogin) {
        if (sesionUi.forzarIrALogin) {
            sesionArranqueViewModel.alConsumirForzarLogin()
            navController.navigate(RutasApp.INICIAR_SESION) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            if (mostrarTopBarCompartida) {
                // Espacio reservado para una top bar compartida route-aware.
            }
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            navController = navController,
            startDestination = RutasApp.SPLASH,
        ) {
            composable(RutasApp.SPLASH) {
                LaunchedEffect(sesionUi.destinoResuelto) {
                    when (val destino = sesionUi.destinoResuelto) {
                        is DestinoSesion.IniciarSesion -> {
                            navController.navigate(RutasApp.INICIAR_SESION) {
                                popUpTo(RutasApp.SPLASH) { inclusive = true }
                            }
                        }
                        is DestinoSesion.Grafo -> {
                            val ruta = when (destino.rol) {
                                RolUsuario.PACIENTE -> RutasApp.GRAFO_PACIENTE
                                RolUsuario.PSICOLOGO -> RutasApp.GRAFO_PSICOLOGO
                                else -> RutasApp.INICIAR_SESION
                            }
                            navController.navigate(ruta) {
                                popUpTo(RutasApp.SPLASH) { inclusive = true }
                            }
                        }
                        null -> Unit
                    }
                }
                PantallaSplash()
            }

            // Si el destino lo resolvió el splash y luego entró un push (tap notif), reaccionamos.
            // Atención: cada grafo (paciente/psicólogo) consume el evento por su cuenta cuando
            // está montado, así que aquí no hacemos nada extra a nivel raíz.

            composable(RutasApp.INICIAR_SESION) {
                PantallaIniciarSesion(
                    alPulsarCrearCuenta = { navController.navigate(RutasApp.REGISTRO_SELECCION_ROL) },
                    alEntrarComoPaciente = {
                        navController.navigate(RutasApp.GRAFO_PACIENTE) {
                            popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                        }
                    },
                    alEntrarComoPsicologo = {
                        navController.navigate(RutasApp.GRAFO_PSICOLOGO) {
                            popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                        }
                    },
                )
            }

            composable(RutasApp.REGISTRO_SELECCION_ROL) {
                PantallaSeleccionRolRegistro(
                    alElegirPaciente = { navController.navigate(RutasApp.REGISTRO_PACIENTE) },
                    alElegirPsicologo = { navController.navigate(RutasApp.REGISTRO_PSICOLOGO) },
                    alVolver = { navController.popBackStack() },
                )
            }

            composable(RutasApp.REGISTRO_PACIENTE) {
                PantallaRegistroPaciente(
                    alRegistroCompletado = {
                        navController.navigate(RutasApp.INICIAR_SESION) {
                            popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                        }
                    },
                    alVolver = { navController.popBackStack() },
                )
            }

            composable(RutasApp.REGISTRO_PSICOLOGO) {
                PantallaRegistroPsicologo(
                    alRegistroCompletado = {
                        navController.navigate(RutasApp.INICIAR_SESION) {
                            popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                        }
                    },
                    alVolver = { navController.popBackStack() },
                )
            }

            composable(RutasApp.GRAFO_PACIENTE) { entradaGrafo ->
                GrafoPacienteNavegacion(
                    navControllerRaiz = navController,
                    entradaGrafo = entradaGrafo,
                )
            }

            composable(RutasApp.GRAFO_PSICOLOGO) { entradaGrafo ->
                GrafoPsicologoNavegacion(
                    navControllerRaiz = navController,
                    entradaGrafo = entradaGrafo,
                )
            }
        }
    }
}
