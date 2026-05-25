package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Canvas

@Composable
fun PantallaConCabeceraOndaApp(
    encabezado: @Composable () -> Unit,
    cabecera: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    colorCabecera: Color? = null,
    paddingEncabezado: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
    alineacionEncabezado: Alignment = Alignment.TopCenter,
    paddingContenido: PaddingValues = PaddingValues(horizontal = 22.dp, vertical = 22.dp),
    proporcionAlturaCabecera: Float = 0.15f,
    alturaOnda: Dp = 72.dp,
    elevacionHoja: Dp = 0.dp,
    contenido: @Composable ColumnScope.() -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Mantener la cabecera estable aunque aparezca el teclado (evita "aplastar" el top).
        val alturaPantallaBase = LocalConfiguration.current.screenHeightDp.dp
        val alturaCabecera = alturaPantallaBase * proporcionAlturaCabecera
        val colorContenidoBajoOnda = MaterialTheme.colorScheme.background
        val colorCabeceraDefecto = MaterialTheme.colorScheme.surface
        val colorCabeceraFinal = colorCabecera ?: colorCabeceraDefecto
        val formaHoja = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        val contenidoCabecera = cabecera ?: encabezado

        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaCabecera)
                    .background(colorCabeceraFinal),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding()
                        .padding(paddingEncabezado),
                    contentAlignment = alineacionEncabezado,
                ) {
                    contenidoCabecera()
                }
            }

            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(alturaOnda)
                    .offset(y = alturaCabecera - 1.dp),
            ) {
                val ancho = size.width
                val alto = size.height
                val ruta = Path().apply {
                    moveTo(0f, 0f)
                    quadraticTo(
                        ancho * 0.25f,
                        alto * 0.85f,
                        ancho * 0.5f,
                        alto * 0.45f,
                    )
                    quadraticTo(
                        ancho * 0.75f,
                        0f,
                        ancho,
                        alto * 0.55f,
                    )
                    lineTo(ancho, alto)
                    lineTo(0f, alto)
                    close()
                }

                drawPath(
                    path = ruta,
                    color = colorContenidoBajoOnda,
                )

                drawLine(
                    color = colorContenidoBajoOnda,
                    start = Offset(0f, alto),
                    end = Offset(ancho, alto),
                    strokeWidth = 2f,
                )
            }

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = alturaCabecera + alturaOnda - 24.dp),
                color = colorContenidoBajoOnda,
                shape = formaHoja,
                tonalElevation = elevacionHoja,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingContenido),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    contenido()
                }
            }
        }
    }
}

