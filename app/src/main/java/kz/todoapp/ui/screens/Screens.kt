package kz.todoapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kz.todoapp.data.Task
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(tasks: List<Task>, onOpenTasks: () -> Unit) {
    val complete = tasks.count { it.isCompleted }
    val pending = tasks.size - complete
    val date = java.time.LocalDate.now().format(
        java.time.format.DateTimeFormatter.ofPattern("d MMMM yyyy", Locale("kk"))
    )
    LazyColumn(
        modifier = Modifier.padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(Modifier.height(12.dp))
            Text("TodoApp", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text("Қош келдіңіз!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
            Text(date, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SummaryCard("Барлығы", tasks.size.toString(), Modifier.weight(1f))
                SummaryCard("Орындалды", complete.toString(), Modifier.weight(1f))
            }
        }
        item { SummaryCard("Орындалмаған", pending.toString(), Modifier.fillMaxWidth()) }
        item {
            Button(onClick = onOpenTasks, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
                Text("Тапсырмаларға өту")
            }
        }
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
        Column(Modifier.padding(18.dp)) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun TasksScreen(tasks: List<Task>, onAdd: (String) -> Unit, onNewTask: () -> Unit, onToggle: (Task, Boolean) -> Unit, onDelete: (Task) -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    Column(Modifier.padding(horizontal = 16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = title, onValueChange = { title = it }, label = { Text("Жаңа тапсырма") },
                modifier = Modifier.weight(1f), singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                shape = RoundedCornerShape(16.dp)
            )
            IconButton(onClick = { if (title.isNotBlank()) { onAdd(title); title = "" } }, modifier = Modifier.padding(start = 4.dp).size(52.dp)) {
                Icon(Icons.Default.Add, contentDescription = "Қосу", tint = MaterialTheme.colorScheme.primary)
            }
        }
        Button(onClick = onNewTask, modifier = Modifier.fillMaxWidth().padding(top = 8.dp), shape = RoundedCornerShape(14.dp)) {
            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(18.dp))
            Text(" Толық мәліметпен қосу")
        }
        Spacer(Modifier.height(12.dp))
        if (tasks.isEmpty()) {
            EmptyMessage("Әзірге тапсырмалар жоқ", "Жаңа тапсырма қосыңыз")
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(tasks, key = { it.id }) { task -> TaskCard(task, onToggle, onDelete) }
            }
        }
    }
}

@Composable
fun CompletedScreen(tasks: List<Task>, onToggle: (Task, Boolean) -> Unit, onDelete: (Task) -> Unit) {
    if (tasks.isEmpty()) {
        EmptyMessage("Орындалған тапсырмалар жоқ", "")
    } else {
        LazyColumn(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(tasks, key = { it.id }) { task ->
                TaskCard(task, onToggle, onDelete, showDate = true)
            }
        }
    }
}

@Composable
private fun TaskCard(task: Task, onToggle: (Task, Boolean) -> Unit, onDelete: (Task) -> Unit, showDate: Boolean = false) {
    Card(shape = RoundedCornerShape(18.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onToggle(task, it) })
            Column(Modifier.weight(1f).padding(start = 4.dp)) {
                Text(
                    task.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                if (task.description.isNotBlank()) Text(task.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (showDate && task.completedAt != null) {
                    Text("Орындалды: ${DateFormat.getDateInstance(DateFormat.MEDIUM, Locale("kk")).format(Date(task.completedAt))}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            IconButton(onClick = { onDelete(task) }) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Жою", tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun NewTaskScreen(onSave: (String, String) -> Unit, onCancel: () -> Unit) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Тапсырма атауы") }, modifier = Modifier.fillMaxWidth(), singleLine = true, shape = RoundedCornerShape(16.dp))
        OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Сипаттамасы (міндетті емес)") }, modifier = Modifier.fillMaxWidth(), minLines = 3, maxLines = 5, shape = RoundedCornerShape(16.dp))
        Button(onClick = { if (title.isNotBlank()) onSave(title, description) }, enabled = title.isNotBlank(), modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Сақтау") }
        TextButton(onClick = onCancel, modifier = Modifier.fillMaxWidth()) { Text("Бас тарту") }
    }
}

@Composable
fun SettingsScreen(darkTheme: Boolean, onThemeChange: (Boolean) -> Unit, onDeleteAll: () -> Unit) {
    var confirmDelete by rememberSaveable { mutableStateOf(false) }
    Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
        Card(shape = RoundedCornerShape(18.dp)) {
            Row(Modifier.fillMaxWidth().padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.TaskAlt, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(32.dp))
                Column(Modifier.padding(start = 14.dp)) {
                    Text("TodoApp", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text("Нұсқасы 1.0", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Card(shape = RoundedCornerShape(18.dp)) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Қараңғы тақырып", modifier = Modifier.weight(1f))
                Switch(checked = darkTheme, onCheckedChange = onThemeChange)
            }
        }
        Button(onClick = { confirmDelete = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) { Text("Барлық тапсырмаларды жою") }
    }
    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Барлық тапсырманы жою керек пе?") },
            text = { Text("Бұл әрекетті қайтару мүмкін емес.") },
            confirmButton = { TextButton(onClick = { onDeleteAll(); confirmDelete = false }) { Text("Жою") } },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Бас тарту") } }
        )
    }
}

@Composable
fun EmptyMessage(title: String, subtitle: String) {
    Column(Modifier.fillMaxWidth().padding(28.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
        if (subtitle.isNotBlank()) Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
