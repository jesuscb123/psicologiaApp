package dam2.tfg.psicologiaapp.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import dam2.tfg.psicologiaapp.R

/**
 * Tipografía alineada con el prototipo: Plus Jakarta Sans (display/headline) e Inter (resto),
 * tamaños según DESIGN.md (display-lg 3.5rem, headline-md 1.75rem, title-lg 1.375rem, body 1rem, label 0.75rem).
 */
private val familiaPlusJakarta = FontFamily(
    Font(R.font.plus_jakarta_sans_variable, FontWeight.Medium),
    Font(R.font.plus_jakarta_sans_variable, FontWeight.SemiBold),
    Font(R.font.plus_jakarta_sans_variable, FontWeight.Bold)
)

private val familiaInter = FontFamily(
    Font(R.font.inter_variable, FontWeight.Normal),
    Font(R.font.inter_variable, FontWeight.Medium),
    Font(R.font.inter_variable, FontWeight.SemiBold)
)

val Typography = Typography(
    displayLarge = TextStyle(
        fontFamily = familiaPlusJakarta,
        fontWeight = FontWeight.Bold,
        fontSize = 56.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.02).em
    ),
    displayMedium = TextStyle(
        fontFamily = familiaPlusJakarta,
        fontWeight = FontWeight.Bold,
        fontSize = 44.sp,
        lineHeight = 52.sp,
        letterSpacing = (-0.015).em
    ),
    displaySmall = TextStyle(
        fontFamily = familiaPlusJakarta,
        fontWeight = FontWeight.SemiBold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = (-0.01).em
    ),

    headlineLarge = TextStyle(
        fontFamily = familiaPlusJakarta,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = (-0.01).em
    ),
    headlineMedium = TextStyle(
        fontFamily = familiaPlusJakarta,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = (-0.005).em
    ),
    headlineSmall = TextStyle(
        fontFamily = familiaPlusJakarta,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.em
    ),

    titleLarge = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.em
    ),
    titleMedium = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.em
    ),
    titleSmall = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.em
    ),

    bodyLarge = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.em
    ),
    bodyMedium = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        letterSpacing = 0.em
    ),
    bodySmall = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 18.sp,
        letterSpacing = 0.em
    ),

    labelLarge = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.08.em
    ),
    labelMedium = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.05.em
    ),
    labelSmall = TextStyle(
        fontFamily = familiaInter,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.05.em
    )
)
