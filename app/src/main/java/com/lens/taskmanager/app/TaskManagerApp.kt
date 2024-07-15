package com.lens.taskmanager.app

import android.app.Application
import com.lens.taskmanager.di.databaseModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class TaskManagerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        loadKoin()
    }

    private fun loadKoin() {
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@TaskManagerApp)
            modules(databaseModule)
        }
    }

    companion object {
        private var instance: TaskManagerApp? = null

        fun getInstance(): TaskManagerApp? {
            if (instance == null) {
                synchronized(TaskManagerApp::class.java) {
                    if (instance == null) {
                        instance = TaskManagerApp()
                    }
                }
            }
            return instance
        }
    }
}
