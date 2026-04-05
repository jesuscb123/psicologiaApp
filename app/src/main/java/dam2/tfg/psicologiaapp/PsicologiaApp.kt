package dam2.tfg.psicologiaapp

import android.app.Application
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class PsicologiaApp : Application() {

    override fun onCreate() {
        super.onCreate()
        // En DEBUG no instalamos App Check: DebugAppCheckProviderFactory dispara con uso frecuente
        // "Too many attempts" y el SDK puede degradar Secure Token / idToken (Storage sigue valiendo con auth).
        // En release, Play Integrity evita abuso en producción.
        if (!BuildConfig.DEBUG) {
            FirebaseAppCheck.getInstance().installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance(),
            )
        }
    }
}

