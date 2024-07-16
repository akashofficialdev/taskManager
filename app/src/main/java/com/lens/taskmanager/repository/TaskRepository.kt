package com.lens.taskmanager.repository

import android.util.Log
import com.lens.taskmanager.data.local.room.TaskDao
import com.lens.taskmanager.data.local.room.TaskEntity

class TaskRepository(private val taskDao: TaskDao) {
    init {
        Log.e("TaskRepository", "TaskRepository created with TaskDao: $taskDao")
    }

    fun getAllTasks() = taskDao.getAllTasks()

    suspend fun insert(task: TaskEntity) {
        taskDao.insert(task)
    }

    suspend fun delete(task: TaskEntity) {
        taskDao.delete(task)
    }

    suspend fun update(task: TaskEntity) {
        taskDao.update(task)
    }

}

