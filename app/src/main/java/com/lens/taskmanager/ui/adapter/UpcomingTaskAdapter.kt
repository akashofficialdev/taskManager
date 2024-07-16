package com.lens.taskmanager.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lens.taskmanager.R
import com.lens.taskmanager.data.local.room.TaskEntity
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class UpcomingTaskAdapter : ListAdapter<TaskEntity, UpcomingTaskAdapter.TaskViewHolder>(
    TaskDiffCallback()
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_upcoming_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title)
        private val dueDateTextView: TextView = itemView.findViewById(R.id.due_date_text)

        fun bind(task: TaskEntity) {
            titleTextView.text = task.title
            dueDateTextView.text = formatDate(task.dueDate)
        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Example output format

            try {
                val date = inputFormat.parse(dateString)
                return outputFormat.format(date)
            } catch (e: ParseException) {
                Log.e("TAG", "Error parsing date: ${e.message}")
                return "" // Handle the error case as per your application's logic
            }
        }
    }

    class TaskDiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
