package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Paleta calmada (claro/oscuro) pensada para fondos muy claros tipo "hielo",
 * superficies blancas y acentos azul/teal. Los nombres `Purple*`/`Pink*` se
 * mantienen como alias temporales para no romper el `Theme.kt` actual.
 */

// -------- Paleta clara --------
val primarioClaro = Color(0xFF1E6FE9) // azul sereno
val enPrimarioClaro = Color(0xFFFFFFFF)
val contenedorPrimarioClaro = Color(0xFFD9E6FF)
val enContenedorPrimarioClaro = Color(0xFF001B3F)

val secundarioClaro = Color(0xFF2A9D8F) // teal calmado
val enSecundarioClaro = Color(0xFFFFFFFF)
val contenedorSecundarioClaro = Color(0xFFBFEDE7)
val enContenedorSecundarioClaro = Color(0xFF00201C)

val terciarioClaro = Color(0xFF4E6FAE) // azul grisáceo
val enTerciarioClaro = Color(0xFFFFFFFF)
val contenedorTerciarioClaro = Color(0xFFDCE7FF)
val enContenedorTerciarioClaro = Color(0xFF0A1B3C)

val fondoClaro = Color(0xFFF5F8FF) // azul hielo
val enFondoClaro = Color(0xFF0E1726)
val superficieClaro = Color(0xFFFFFFFF)
val enSuperficieClaro = Color(0xFF0E1726)
val varianteSuperficieClaro = Color(0xFFE7EEF8)
val enVarianteSuperficieClaro = Color(0xFF3C4658)
val contornoClaro = Color(0xFFB4C2D6)
val contornoVarianteClaro = Color(0xFFD6DFEC)

val errorClaro = Color(0xFFBA1A1A)
val enErrorClaro = Color(0xFFFFFFFF)
val contenedorErrorClaro = Color(0xFFFFDAD6)
val enContenedorErrorClaro = Color(0xFF410002)

// -------- Paleta oscura --------
val primarioOscuro = Color(0xFF7FB4FF)
val enPrimarioOscuro = Color(0xFF002A5A)
val contenedorPrimarioOscuro = Color(0xFF003B7F)
val enContenedorPrimarioOscuro = Color(0xFFD9E6FF)

val secundarioOscuro = Color(0xFF6FD6C8)
val enSecundarioOscuro = Color(0xFF003732)
val contenedorSecundarioOscuro = Color(0xFF005049)
val enContenedorSecundarioOscuro = Color(0xFFBFEDE7)

val terciarioOscuro = Color(0xFFB3C5FF)
val enTerciarioOscuro = Color(0xFF1A2A52)
val contenedorTerciarioOscuro = Color(0xFF32406A)
val enContenedorTerciarioOscuro = Color(0xFFDCE7FF)

val fondoOscuro = Color(0xFF0B1220) // azul petróleo muy oscuro
val enFondoOscuro = Color(0xFFE7ECF7)
val superficieOscuro = Color(0xFF0F1A2B)
val enSuperficieOscuro = Color(0xFFE7ECF7)
val varianteSuperficieOscuro = Color(0xFF1E2A3F)
val enVarianteSuperficieOscuro = Color(0xFFB9C6DD)
val contornoOscuro = Color(0xFF7E8DA8)
val contornoVarianteOscuro = Color(0xFF2B3851)

val errorOscuro = Color(0xFFFFB4AB)
val enErrorOscuro = Color(0xFF690005)
val contenedorErrorOscuro = Color(0xFF93000A)
val enContenedorErrorOscuro = Color(0xFFFFDAD6)

// -------- Aliases (tema actual) --------
val Purple40 = primarioClaro
val PurpleGrey40 = secundarioClaro
val Pink40 = terciarioClaro

val Purple80 = primarioOscuro
val PurpleGrey80 = secundarioOscuro
val Pink80 = terciarioOscuro