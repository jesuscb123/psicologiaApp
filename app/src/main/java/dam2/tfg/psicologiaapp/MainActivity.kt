package dam2.tfg.psicologiaapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.ColaDestinosNotificacion
import dam2.tfg.psicologiaapp.presentation.ui.notificaciones.GestorCanalesNotificacion
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
        // Aseguramos los canales en cuanto la actividad arranca: si la primera notificación es
        // local (foreground) los necesita ya creados.
        GestorCanalesNotificacion.asegurarCanales(applicationContext)
        // Si el sistema arrancó la actividad por un tap en notificación, leemos los extras
        // y los dejamos para que la navegación los consuma cuando esté lista.
        ColaDestinosNotificacion.publicarDesdeIntentSiProcede(intent)

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

    /**
     * Cuando la actividad ya estaba viva (singleTop/clearTop) el SO entrega aquí el nuevo
     * intent del tap en la notificación, así que también hay que reenviarlo a la cola.
     */
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        ColaDestinosNotificacion.publicarDesdeIntentSiProcede(intent)
    }
}