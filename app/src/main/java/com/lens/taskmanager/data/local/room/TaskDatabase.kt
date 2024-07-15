package com.lens.taskmanager.data.local.room

import android.util.Log
import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [TaskEntity::class], version = 1, exportSchema = false)
abstract class TaskDatabase : RoomDatabase() {
    init {
        Log.e("TaskDatabase", "TaskDatabase created")
    }


    abstract fun taskDao(): TaskDao
}
