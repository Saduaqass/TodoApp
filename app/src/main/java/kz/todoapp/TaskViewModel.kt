package kz.todoapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kz.todoapp.data.AppDatabase
import kz.todoapp.data.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = AppDatabase.getInstance(application).taskDao()
    val tasks = dao.observeAll().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val completedTasks = dao.observeCompleted().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addTask(title: String, description: String = "") {
        val cleanTitle = title.trim()
        if (cleanTitle.isEmpty()) return
        viewModelScope.launch { dao.insert(Task(title = cleanTitle, description = description.trim())) }
    }

    fun setCompleted(task: Task, completed: Boolean) {
        viewModelScope.launch { dao.setCompleted(task.id, completed, if (completed) System.currentTimeMillis() else null) }
    }

    fun delete(task: Task) { viewModelScope.launch { dao.delete(task.id) } }
    fun deleteAll() { viewModelScope.launch { dao.deleteAll() } }
}
