package dam2.tfg.psicologiaapp.presentation.navegacion

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dam2.tfg.psicologiaapp.presentation.ui.inicio.PantallaIniciarSesion
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaRegistroPaciente
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaRegistroPsicologo
import dam2.tfg.psicologiaapp.presentation.ui.registro.PantallaSeleccionRolRegistro

@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = RutasApp.INICIAR_SESION,
    ) {
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
