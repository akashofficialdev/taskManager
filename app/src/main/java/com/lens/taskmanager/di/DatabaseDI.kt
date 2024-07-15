package com.lens.taskmanager.di

import android.content.Context
import androidx.room.Room
import com.lens.taskmanager.data.local.room.TaskDatabase
import com.lens.taskmanager.utils.TASK_DATABASE

fun provideDatabase(context: Context) =
    Room.databaseBuilder(context, TaskDatabase::class.java, TASK_DATABASE)
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

fun provideDao(db: TaskDatabase) = db.taskDao()