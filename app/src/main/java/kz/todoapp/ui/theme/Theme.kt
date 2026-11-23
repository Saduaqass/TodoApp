package kz.todoapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val Navy = Color(0xFF193653)
private val Blue = Color(0xFF2563EB)
private val lightColors = lightColorScheme(
    primary = Blue, onPrimary = Color.White, secondary = Navy,
    background = Color(0xFFF8FAFC), surface = Color.White,
    onBackground = Color(0xFF1E293B), onSurface = Color(0xFF1E293B),
    surfaceVariant = Color(0xFFE9EEF3)
)
private val darkColors = darkColorScheme(
    primary = Color(0xFF8AB4FF), onPrimary = Color(0xFF062E68), secondary = Color(0xFFB7D3F2),
    background = Color(0xFF101923), surface = Color(0xFF182431),
    onBackground = Color(0xFFE3EAF2), onSurface = Color(0xFFE3EAF2),
    surfaceVariant = Color(0xFF263544)
)

@Composable
fun TodoTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) darkColors else lightColors, content = content)
}
