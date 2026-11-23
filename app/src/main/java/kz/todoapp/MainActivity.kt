package kz.todoapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kz.todoapp.ui.theme.TodoTheme

class MainActivity : ComponentActivity() {
    private var darkTheme by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val preferences = getSharedPreferences("todo_settings", MODE_PRIVATE)
        darkTheme = preferences.getBoolean("dark_theme", false)
        setContent {
            TodoTheme(darkTheme = darkTheme) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    TodoApp(
                        darkTheme = darkTheme,
                        onThemeChange = {
                            darkTheme = it
                            preferences.edit().putBoolean("dark_theme", it).apply()
                        }
                    )
                }
            }
        }
    }
}
