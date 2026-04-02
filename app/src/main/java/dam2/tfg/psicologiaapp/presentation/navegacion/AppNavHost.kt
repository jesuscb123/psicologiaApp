package dam2.tfg.psicologiaapp.presentation.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dam2.tfg.psicologiaapp.presentation.ui.inicio.PantallaIniciarSesion
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaAnadirNota
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaHomePaciente
import dam2.tfg.psicologiaapp.presentation.ui.paciente.PantallaPerfilPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.psicologo.PantallaPsicologoPlaceholder
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaRegistroPaciente
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaRegistroPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaSeleccionRolRegistro

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasApp.INICIAR_SESION
    ) {
        composable(RutasApp.INICIAR_SESION) {
            PantallaIniciarSesion(
                alPulsarCrearCuenta = { navController.navigate(RutasApp.REGISTRO_SELECCION_ROL) },
                alEntrarComoPaciente = {
                    navController.navigate(RutasApp.HOME_PACIENTE) {
                        popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                    }
                },
                alEntrarComoPsicologo = {
                    navController.navigate(RutasApp.PLACEHOLDER_PSICOLOGO) {
                        popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                    }
                }
            )
        }

        composable(RutasApp.REGISTRO_SELECCION_ROL) {
            PantallaSeleccionRolRegistro(
                alElegirPaciente = { navController.navigate(RutasApp.REGISTRO_PACIENTE) },
                alElegirPsicologo = { navController.navigate(RutasApp.REGISTRO_PSICOLOGO) },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(RutasApp.REGISTRO_PACIENTE) {
            PantallaRegistroPaciente(
                alRegistroCompletado = {
                    navController.navigate(RutasApp.INICIAR_SESION) {
                        popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                    }
                },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(RutasApp.REGISTRO_PSICOLOGO) {
            PantallaRegistroPsicologo(
                alRegistroCompletado = {
                    navController.navigate(RutasApp.INICIAR_SESION) {
                        popUpTo(RutasApp.INICIAR_SESION) { inclusive = true }
                    }
                },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(RutasApp.HOME_PACIENTE) {
            PantallaHomePaciente(
                alIrAPerfilPsicologo = { psicologoId ->
                    navController.navigate(RutasApp.crearRutaPerfilPsicologo(psicologoId))
                },
                alIrAAnadirNota = { navController.navigate(RutasApp.ANADIR_NOTA) }
            )
        }

        composable(
            route = RutasApp.PERFIL_PSICOLOGO,
            arguments = listOf(
                navArgument(RutasApp.ARG_PSICOLOGO_ID) { type = NavType.StringType }
            )
        ) { entrada ->
            val psicologoId = entrada.arguments?.getString(RutasApp.ARG_PSICOLOGO_ID).orEmpty()
            PantallaPerfilPsicologo(
                psicologoId = psicologoId,
                alAsignacionCompletada = {
                    navController.navigate(RutasApp.HOME_PACIENTE) {
                        popUpTo(RutasApp.HOME_PACIENTE) { inclusive = true }
                    }
                },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(RutasApp.ANADIR_NOTA) {
            PantallaAnadirNota(
                alNotaGuardada = { navController.popBackStack() },
                alVolver = { navController.popBackStack() }
            )
        }

        composable(RutasApp.PLACEHOLDER_PSICOLOGO) {
            PantallaPsicologoPlaceholder(
                alCerrarSesion = {
                    navController.navigate(RutasApp.INICIAR_SESION) {
                        popUpTo(0)
                    }
                }
            )
        }
    }
}

