package com.nestor.futbolia.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val NestorColors =
    darkColorScheme(

        primary = Color(0xFF19A7FF),

        onPrimary = Color.White,

        secondary = Color(0xFF00D084),

        onSecondary = Color.Black,

        background = Color(0xFF06111D),

        onBackground = Color.White,

        surface = Color(0xFF0B1B2B),

        onSurface = Color.White,

        surfaceVariant = Color(0xFF12283D),

        onSurfaceVariant = Color(0xFFB7C7D6),

        error = Color(0xFFFF4D5A),

        onError = Color.White
    )

@Composable
fun FutbolTheme(
    content: @Composable () -> Unit
) {

    MaterialTheme(

        colorScheme = NestorColors,

        typography = Typography(),

        content = content
    )
}
