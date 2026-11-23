package kz.todoapp.navigation

sealed class Route(val path: String, val label: String) {
    data object Home : Route("home", "Басты бет")
    data object Tasks : Route("tasks", "Тапсырмалар")
    data object NewTask : Route("new_task", "Жаңа тапсырма")
    data object Completed : Route("completed", "Орындалғандар")
    data object Settings : Route("settings", "Баптаулар")
}
