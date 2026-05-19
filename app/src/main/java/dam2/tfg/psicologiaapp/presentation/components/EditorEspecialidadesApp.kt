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
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

private val CHIP_SHAPE = RoundedCornerShape(999.dp)

/**
 * Editor de especialidades estilo "hashtag": chips eliminables + burbuja de entrada integrada
 * en el mismo FlowRow, sin botón Add externo.
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
) {
    val inputHabilitado = habilitado && especialidades.size < maxEspecialidades

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
                            modifier = Modifier.widthIn(min = 80.dp, max = 160.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (especialidadInput.isEmpty()) {
                                Text(
                                    text = "Nueva especialidad…",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            BasicTextField(
                                value = especialidadInput,
                                onValueChange = alCambiarInput,
                                singleLine = true,
                                textStyle = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = { alAnadir() }),
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
