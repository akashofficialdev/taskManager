package com.lens.taskmanager.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lens.taskmanager.R
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.databinding.ItemCreatedTaskBinding
import com.lens.taskmanager.utils.Priority


class TaskAdapter : RecyclerView.Adapter<TaskAdapter.ViewHolder>() {

    interface OnEditClickListener {
        fun onEditClick(task: TaskEntity)
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(task: TaskEntity)
    }

    interface OnItemClickListener {
        fun onItemClick(task: TaskEntity)
    }

    interface OnCheckClickListener {
        fun onCheckClick(task: TaskEntity, isChecked: Boolean)
    }

    private lateinit var binding: ItemCreatedTaskBinding
    private lateinit var context: Context
    private var onEditClickListener: OnEditClickListener? = null
    private var onDeleteClickListener: OnDeleteClickListener? = null
    private var onItemClickListener: OnItemClickListener? = null
    private var onCheckClickListener: OnCheckClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        binding = ItemCreatedTaskBinding.inflate(inflater, parent, false)
        context = parent.context
        return ViewHolder()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
        holder.setIsRecyclable(false)
    }

    override fun getItemCount(): Int = differ.currentList.size

    inner class ViewHolder : RecyclerView.ViewHolder(binding.root) {


        fun bind(item: TaskEntity) {
            binding.apply {
                Log.e("TAG", "bind: item is $item")
                tvTaskTitle.text = item.title
                tvDueDate.text = item.dueDate
                tvPriorityLevel.text = item.priority
                when (item.priority) {
                    Priority.HIGH -> tvPriorityLevel.setBackgroundResource(R.drawable.priority_high)
                    Priority.LOW -> tvPriorityLevel.setBackgroundResource(R.drawable.priority_low)
                    else -> tvPriorityLevel.setBackgroundResource(R.drawable.priority_medium) // Optional default background
                }

                // Set click listeners for edit and delete buttons
                btnEdit.setOnClickListener {
                    onEditClickListener?.onEditClick(item)
                }

                btnDelete.setOnClickListener {
                    onDeleteClickListener?.onDeleteClick(item)
                }
                cvTask.setOnClickListener {
                    onItemClickListener?.onItemClick(item)
                }

                checkboxCompleted.apply {
                    setOnCheckedChangeListener(null)
                    isChecked = item.isCompleted
                    setOnCheckedChangeListener { _, isChecked ->
                        Log.e("TAG", "bind: oncheckchange: $isChecked")
                        onCheckClickListener?.onCheckClick(item, isChecked)
                    }
                }
            }
        }
    }

    private val differCallback = object : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    // Setters for the click listeners
    fun setOnEditClickListener(listener: OnEditClickListener) {
        onEditClickListener = listener
    }

    fun setOnDeleteClickListener(listener: OnDeleteClickListener) {
        onDeleteClickListener = listener
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }

    fun setOnCheckClickListener(listener: OnCheckClickListener) {
        onCheckClickListener = listener
    }
}

