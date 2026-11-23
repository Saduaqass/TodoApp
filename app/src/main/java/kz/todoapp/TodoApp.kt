package kz.todoapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kz.todoapp.navigation.Route
import kz.todoapp.ui.screens.CompletedScreen
import kz.todoapp.ui.screens.HomeScreen
import kz.todoapp.ui.screens.NewTaskScreen
import kz.todoapp.ui.screens.SettingsScreen
import kz.todoapp.ui.screens.TasksScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodoApp(darkTheme: Boolean, onThemeChange: (Boolean) -> Unit, taskViewModel: TaskViewModel = viewModel()) {
    val navController = rememberNavController()
    val tasks by taskViewModel.tasks.collectAsState()
    val completed by taskViewModel.completedTasks.collectAsState()
    val backStack by navController.currentBackStackEntryAsState()
    val current = backStack?.destination?.route ?: Route.Home.path
    val bottomRoutes = listOf(Route.Home, Route.Tasks, Route.Completed, Route.Settings)
    val pageTitle = when (current) {
        Route.Tasks.path -> Route.Tasks.label
        Route.NewTask.path -> Route.NewTask.label
        Route.Completed.path -> Route.Completed.label
        Route.Settings.path -> Route.Settings.label
        else -> Route.Home.label
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text(pageTitle, style = MaterialTheme.typography.titleLarge) }) },
        bottomBar = {
            if (current != Route.NewTask.path) {
                NavigationBar {
                    bottomRoutes.forEach { route ->
                        val icon = when (route) {
                            Route.Home -> Icons.Default.Home
                            Route.Tasks -> Icons.Default.TaskAlt
                            Route.Completed -> Icons.Default.CheckCircle
                            else -> Icons.Default.Settings
                        }
                        NavigationBarItem(
                            selected = current == route.path,
                            onClick = {
                                navController.navigate(route.path) {
                                    popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { androidx.compose.material3.Icon(icon, contentDescription = route.label) },
                            label = { Text(route.label) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(navController, startDestination = Route.Home.path, modifier = Modifier.padding(innerPadding)) {
            composable(Route.Home.path) {
                HomeScreen(tasks) { navController.navigate(Route.Tasks.path) }
            }
            composable(Route.Tasks.path) {
                TasksScreen(
                    tasks = tasks,
                    onAdd = { taskViewModel.addTask(it) },
                    onNewTask = { navController.navigate(Route.NewTask.path) },
                    onToggle = taskViewModel::setCompleted,
                    onDelete = taskViewModel::delete
                )
            }
            composable(Route.NewTask.path) {
                NewTaskScreen(
                    onSave = { title, description ->
                        taskViewModel.addTask(title, description)
                        navController.popBackStack()
                    },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Route.Completed.path) {
                CompletedScreen(completed, taskViewModel::setCompleted, taskViewModel::delete)
            }
            composable(Route.Settings.path) {
                SettingsScreen(darkTheme, onThemeChange, taskViewModel::deleteAll)
            }
        }
    }
}
