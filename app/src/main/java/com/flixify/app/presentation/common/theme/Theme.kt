package com.flixify.app.presentation.common.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Colors - Exact match to Windows Qt app
val Background = Color(0xFF05070B)
val Surface = Color(0xFF090C13)
val SurfaceVariant = Color(0xFF131923)
val Panel = Color(0xF0080B11)

val Accent = Color(0xFFE50914)
val AccentStrong = Color(0xFFFF2432)
val AccentSoft = Color(0xFFE50914).copy(alpha = 0.13f)

val TextPrimary = Color(0xFFF7F8FB)
val TextMuted = Color(0xFFB1BAC9)
val TextSecondary = Color(0xFF94A3B8)

val Border = Color(0x33FFFFFF)
val BorderSoft = Color(0x1AFFFFFF)

val Success = Color(0xFF30D19D)
val Error = Color(0xFFFF7D86)
val Info = Color(0xFF7CB6FF)
val Warning = Color(0xFFFBBF24)

val ButtonPrimary = Color(0xFFE50914)
val ButtonPrimaryPressed = Color(0xFFB91C1C)
val ButtonSecondary = Color(0xFF1F2937)
val ButtonSecondaryPressed = Color(0xFF374151)

val GlassBackground = Color(0xCC0D121C)

// Shapes
val GlassCardShape = RoundedCornerShape(24.dp)
val InputShape = RoundedCornerShape(20.dp)
val ButtonShape = RoundedCornerShape(16.dp)

// Typography
val FlixifyTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

// Color Scheme
private val DarkColorScheme = darkColorScheme(
    primary = Accent,
    onPrimary = TextPrimary,
    primaryContainer = AccentSoft,
    onPrimaryContainer = TextPrimary,
    secondary = SurfaceVariant,
    onSecondary = TextPrimary,
    secondaryContainer = SurfaceVariant,
    onSecondaryContainer = TextPrimary,
    tertiary = Info,
    onTertiary = Background,
    tertiaryContainer = Info.copy(alpha = 0.1f),
    onTertiaryContainer = Info,
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariant,
    onSurfaceVariant = TextMuted,
    error = Error,
    onError = TextPrimary,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,
    outline = Border,
    outlineVariant = BorderSoft,
    inverseOnSurface = TextPrimary,
    inverseSurface = Surface,
    inversePrimary = AccentStrong,
    surfaceTint = Accent
)

@Composable
fun FlixifyTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = FlixifyTypography,
        content = content
    )
}
