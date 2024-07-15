package com.lens.taskmanager.features

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.android.edique.edxpert.features.base.BaseViewModel
import com.lens.taskmanager.data.local.room.TaskEntity
import kotlinx.coroutines.launch

class TaskViewModel(
    private val repository: TaskRepository
) : BaseViewModel() {

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
}
