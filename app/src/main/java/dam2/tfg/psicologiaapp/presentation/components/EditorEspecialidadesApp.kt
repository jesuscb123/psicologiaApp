package dam2.tfg.psicologiaapp.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import dam2.tfg.psicologiaapp.presentation.ui.registro.util.LimitesCaracteresRegistro

private val CHIP_SHAPE = RoundedCornerShape(999.dp)

/**
 * Editor de especialidades estilo "hashtag": chips eliminables + burbuja de entrada integrada
 * en el mismo FlowRow, sin botón Add externo.
 *
 * El ancho del campo crece con el texto escrito (hasta el máximo que caben
 * [maxCaracteresInput] caracteres con el estilo actual).
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditorEspecialidadesApp(
    especialidades: List<String>,
    especialidadInput: String,
    errorEspecialidadInput: String?,
    habilitado: Boolean,
    maxEspecialidades: Int,
    alCambiarInput: (String) -> Unit,
    alAnadir: () -> Unit,
    alEliminar: (Int) -> Unit,
    modifier: Modifier = Modifier,
    formaEntrada: Shape = CHIP_SHAPE,
    /** Máximo de caracteres por especialidad (debe coincidir con el ViewModel / [LimitesCaracteresRegistro]). */
    maxCaracteresInput: Int = LimitesCaracteresRegistro.Psicologo.ESPECIALIDAD,
    textoPlaceholderInput: String = "añadir",
) {
    val inputHabilitado = habilitado && especialidades.size < maxEspecialidades

    val textStyleInput = MaterialTheme.typography.bodyMedium.copy(
        color = MaterialTheme.colorScheme.onSurface,
    )
    val textMeasurer = rememberTextMeasurer()
    val densidad = LocalDensity.current

    val anchoCampoDp = remember(
        especialidadInput,
        maxCaracteresInput,
        textoPlaceholderInput,
        textStyleInput.fontSize,
        textStyleInput.fontWeight,
        textStyleInput.fontFamily,
        densidad.density,
        densidad.fontScale,
    ) {
        val constraintsSinTope = Constraints(maxWidth = Int.MAX_VALUE)

        fun anchoPxDe(texto: String): Int =
            textMeasurer.measure(
                AnnotatedString(texto),
                style = textStyleInput,
                overflow = TextOverflow.Clip,
                softWrap = false,
                maxLines = 1,
                constraints = constraintsSinTope,
            ).size.width

        val textoVisible = especialidadInput.ifEmpty { textoPlaceholderInput }
            .take(maxCaracteresInput)
        val anchoTextoPx = anchoPxDe(textoVisible)
        val anchoTopePorMaxCaracteres = anchoPxDe("W".repeat(maxCaracteresInput))
        val extraCursorPx = with(densidad) { 6.dp.roundToPx() }

        val anchoClampedPx = (anchoTextoPx + extraCursorPx).coerceIn(
            minimumValue = anchoPxDe(textoPlaceholderInput) + extraCursorPx,
            maximumValue = anchoTopePorMaxCaracteres + extraCursorPx,
        )
        with(densidad) { anchoClampedPx.toDp() }
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Chips de especialidades ya añadidas
            especialidades.forEachIndexed { index, especialidad ->
                InputChip(
                    selected = false,
                    onClick = {},
                    label = { Text(especialidad) },
                    trailingIcon = {
                        IconButton(
                            onClick = { alEliminar(index) },
                            enabled = habilitado,
                            modifier = Modifier.size(18.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Eliminar $especialidad",
                                modifier = Modifier.size(14.dp),
                            )
                        }
                    },
                )
            }

            // Burbuja de entrada: solo visible si no se ha alcanzado el máximo
            if (inputHabilitado) {
                Surface(
                    shape = formaEntrada,
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (errorEspecialidadInput != null)
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.outline,
                    ),
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Box(
                            modifier = Modifier.width(anchoCampoDp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            BasicTextField(
                                value = especialidadInput,
                                onValueChange = { nuevo ->
                                    if (nuevo.length <= maxCaracteresInput) {
                                        alCambiarInput(nuevo)
                                    }
                                },
                                singleLine = true,
                                textStyle = textStyleInput,
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { alAnadir() }),
                                decorationBox = { innerTextField ->
                                    Box(contentAlignment = Alignment.CenterStart) {
                                        if (especialidadInput.isEmpty()) {
                                            Text(
                                                text = textoPlaceholderInput,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            )
                                        }
                                        innerTextField()
                                    }
                                },
                            )
                        }
                        IconButton(
                            onClick = alAnadir,
                            enabled = especialidadInput.isNotBlank(),
                            modifier = Modifier.size(18.dp),
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = "Añadir especialidad",
                                tint = if (especialidadInput.isNotBlank())
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }

        // Mensaje de error bajo el FlowRow
        if (errorEspecialidadInput != null) {
            Text(
                text = errorEspecialidadInput,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 4.dp),
            )
        }
    }
}
