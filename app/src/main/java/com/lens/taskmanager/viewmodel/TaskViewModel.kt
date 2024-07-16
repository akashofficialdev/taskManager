package com.lens.taskmanager.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.edique.edxpert.features.base.BaseViewModel
import com.google.android.gms.tasks.Task
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.data.remote.network.GoogleGeminiService
import com.lens.taskmanager.repository.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : BaseViewModel() {
    private val apiKey = "AIzaSyDdZmOqadnPwN0bTn7Z_fJqMZF77dGmnm0"
    private val geminiService = GoogleGeminiService(apiKey)

    init {
        Log.e("TaskViewModel", "TaskViewModel created with TaskRepository: $repository")
    }

    private val _taskList = MutableLiveData<List<TaskEntity>>()
    val taskList: LiveData<List<TaskEntity>>
        get() = _taskList

    fun getAllTask()=viewModelScope.launch {
        repository.getAllTasks().collect{
            _taskList.postValue(it)
        }
    }

    fun insertTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.insert(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.delete(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.update(task)
        }
    }

    fun fetchTaskSuggestion(userBehaviorData: List<TaskEntity>, callback: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val suggestion = geminiService.generateTaskSuggestion(userBehaviorData)
                callback(suggestion ?: "No suggestion available")
            } catch (e: Exception) {
                println("Failed to get suggestion: ${e.message}")
                callback("Failed to get suggestion")
            }
        }
    }
}
