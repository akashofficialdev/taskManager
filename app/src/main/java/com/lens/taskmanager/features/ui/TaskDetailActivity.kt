package com.lens.taskmanager.features.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.lens.taskmanager.R
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.databinding.ActivityCreateTaskBinding
import com.lens.taskmanager.databinding.ActivityTaskDetailBinding
import com.lens.taskmanager.features.TaskAdapter
import com.lens.taskmanager.features.TaskViewModel
import com.lens.taskmanager.features.base.BaseActivity
import com.lens.taskmanager.utils.Priority
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskDetailActivity : BaseActivity<TaskViewModel, ActivityTaskDetailBinding>() {

    override val mViewModel: TaskViewModel by viewModel()

    override fun getViewBinding(): ActivityTaskDetailBinding =
        ActivityTaskDetailBinding.inflate(layoutInflater)

    private val editTaskContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

    }

    override fun initUI() {
        val task: TaskEntity? = intent.getSerializableExtra("task_entity") as? TaskEntity

        if (task != null) {
            mViewBinding.apply {
                taskTitle.text = task.title
                taskDescription.text = task.description
                dueDate.text = task.dueDate
                priorityLevel.text = task.priority
                switchCompleted.isChecked = task.isCompleted
                when (task.priority) {
                    Priority.HIGH -> priorityLevel.setBackgroundResource(R.drawable.priority_high)
                    Priority.LOW -> priorityLevel.setBackgroundResource(R.drawable.priority_low)
                    else -> priorityLevel.setBackgroundResource(R.drawable.priority_medium) // Optional default background
                }
            }
        }

        mViewBinding.btnEditTask.setOnClickListener {
            val intent = Intent(this, CreateTaskActivity::class.java)
            intent.putExtra("task_entity", task)
            editTaskContract.launch(intent)
        }

        mViewBinding.btnDeleteTask.setOnClickListener {
            task?.let { it1 -> mViewModel.deleteTask(it1) }
            Toast.makeText(this,"Task deleted",Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_OK)
            finish()
        }
    }
}