package com.lens.taskmanager.di

import com.lens.taskmanager.repository.TaskRepository
import com.lens.taskmanager.viewmodel.TaskViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val databaseModule = module {
    single {
        println("Providing Database")
        provideDatabase(androidContext())
    }
    single {
        println("Providing DAO")
        provideDao(get())
    }
    factory {
        println("Providing TaskRepository")
        TaskRepository(get())
    }
    viewModel {
        println("Providing TaskViewModel")
        TaskViewModel(get())
    }
}
