package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.ui.unit.dp

/**
 * Espaciados alineados con Tailwind/CSS del prototipo Stitch y con
 * [doc/stitch_mindbridge_therapy_hub/serene_mindset/DESIGN.md] (márgenes 24–32dp,
 * padding interno de tarjetas 24dp).
 */
object EspaciadoMindful {
    val xs = 4.dp
    val sm = 8.dp
    val md = 16.dp
    val lg = 24.dp
    val xl = 32.dp
    val xxl = 48.dp

    /** `p-6` / secciones con “respiración” (24.dp) */
    val pantallaHorizontal = 24.dp
    val pantallaVertical = 24.dp

    /** Separador de zonas (DESIGN: 24–32px) */
    val seccion = 32.dp

    /** Contenido dentro de cards (DESIGN: 24px vertical) */
    val tarjetaInterno = 24.dp

    /** `gap-5` en formularios login */
    val formularioGap = 20.dp

    /** Radios CSS: default 1rem, input ~1.5rem, card lg 2rem, FAB xl 3rem */
    val radioInput = 24.dp
    val radioTarjeta = 32.dp
    val radioFab = 48.dp
}
