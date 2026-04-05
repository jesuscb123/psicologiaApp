package dam2.tfg.psicologiaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import dam2.tfg.psicologiaapp.preferencias.domain.model.ModoTemaApp
import dam2.tfg.psicologiaapp.presentation.ModoTemaActividadViewModel
import dam2.tfg.psicologiaapp.presentation.navegacion.AppNavHost
import dam2.tfg.psicologiaapp.ui.theme.PsicologiaappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val modoTemaActividadViewModel: ModoTemaActividadViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val modoTema by modoTemaActividadViewModel.modoTema.collectAsState()
            val sistemaEnOscuro = isSystemInDarkTheme()
            val usarTemaOscuro = when (modoTema) {
                ModoTemaApp.SeguirSistema -> sistemaEnOscuro
                ModoTemaApp.Claro -> false
                ModoTemaApp.Oscuro -> true
            }
            PsicologiaappTheme(darkTheme = usarTemaOscuro) {
                AppNavHost()
            }
        }
    }
}