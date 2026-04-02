package dam2.tfg.psicologiaapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dam2.tfg.psicologiaapp.presentation.navegacion.AppNavHost
import dam2.tfg.psicologiaapp.ui.theme.PsicologiaappTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PsicologiaappTheme {
                AppNavHost()
            }
        }
    }
}