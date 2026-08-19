package com.iamnaimul.amarmanibag

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp

private val Bengali = FontFamily.SansSerif

private val LightColors = lightColorScheme(
    background = androidx.compose.ui.graphics.Color(0xFFFAF9F6),
    surface = androidx.compose.ui.graphics.Color(0xFFFAF9F6),
    primary = androidx.compose.ui.graphics.Color(0xFF00695C),
    secondary = androidx.compose.ui.graphics.Color(0xFF00897B),
    tertiary = androidx.compose.ui.graphics.Color(0xFF4E635F)
)
private val DarkColors = darkColorScheme(
    primary = androidx.compose.ui.graphics.Color(0xFF66D9CC),
    secondary = androidx.compose.ui.graphics.Color(0xFF4DB6AC),
    tertiary = androidx.compose.ui.graphics.Color(0xFFB1CCC7)
)

@Composable
fun AmarManibagTheme(mode: String, content: @Composable () -> Unit) {
    val dark = when (mode) {
        "dark" -> true
        "light" -> false
        else -> isSystemInDarkTheme()
    }
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        typography = Typography(
            bodyLarge = TextStyle(fontFamily = Bengali, fontSize = 16.sp),
            bodyMedium = TextStyle(fontFamily = Bengali, fontSize = 14.sp),
            titleLarge = TextStyle(fontFamily = Bengali, fontWeight = FontWeight.Bold, fontSize = 22.sp),
            titleMedium = TextStyle(fontFamily = Bengali, fontWeight = FontWeight.Bold, fontSize = 17.sp),
            labelLarge = TextStyle(fontFamily = Bengali, fontWeight = FontWeight.Bold)
        ),
        content = content
    )
}
