package com.flixify.app.presentation.common.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

// Custom shapes for specific components
val CodeCardShape = RoundedCornerShape(18.dp)
val GlassCardShape = RoundedCornerShape(28.dp)
val ButtonShape = RoundedCornerShape(8.dp)
val InputShape = RoundedCornerShape(16.dp)
val PosterShape = RoundedCornerShape(24.dp)
val AvatarShape = RoundedCornerShape(22.dp)
val ChipShape = RoundedCornerShape(8.dp)
