package com.lens.taskmanager.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lens.taskmanager.utils.TASK_TABLE
import java.io.Serializable

@Entity(tableName = TASK_TABLE)
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int=0,
    val title: String,
    val description: String,
    val dueDate: String,
    val priority: String,
    val isCompleted: Boolean,
    val latitude: Double,
    val longitude: Double
):Serializable
