package com.lens.taskmanager.features.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.databinding.FragmentHomeBinding
import com.lens.taskmanager.features.TaskAdapter
import com.lens.taskmanager.features.TaskViewModel
import com.lens.taskmanager.features.base.BaseFragment
import com.lens.taskmanager.helper.BiometricHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : BaseFragment<FragmentHomeBinding, TaskViewModel>() {
    private val taskAdapter by lazy { TaskAdapter() }
    override val mViewModel: TaskViewModel by viewModel()
    private val addTaskContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                mViewModel.getAllTask()
            }
        }

    override fun initUI() {
        binding.apply {
            btnAddTask.setOnClickListener {
                addTaskContract.launch(Intent(requireContext(), CreateTaskActivity::class.java))
            }
            btnLeaveApply.setOnClickListener {
                addTaskContract.launch(Intent(requireContext(), CreateTaskActivity::class.java))
            }

            mViewModel.getAllTask()
            mViewModel.taskList.observe(viewLifecycleOwner) {
                if (it.isNotEmpty()) {
                    clPlaceholder.isVisible = false
                    clTask.isVisible = true
                    taskAdapter.differ.submitList(it)
                    rvTask.apply {
                        layoutManager = LinearLayoutManager(requireContext())
                        adapter = taskAdapter
                    }
                } else {
                    clPlaceholder.isVisible = true
                    clTask.isVisible = false
                }
            }
        }

        taskAdapter.setOnEditClickListener(object : TaskAdapter.OnEditClickListener {
            override fun onEditClick(task: TaskEntity) {
                // Handle edit click
                val intent = Intent(requireContext(), CreateTaskActivity::class.java)
                intent.putExtra("task_entity", task)
                addTaskContract.launch(intent)
            }
        })

        taskAdapter.setOnDeleteClickListener(object : TaskAdapter.OnDeleteClickListener {
            override fun onDeleteClick(task: TaskEntity) {
                mViewModel.deleteTask(task)
                mViewModel.getAllTask()
            }
        })

        taskAdapter.setOnItemClickListener(object : TaskAdapter.OnItemClickListener {
            override fun onItemClick(task: TaskEntity) {
                val intent = Intent(requireContext(), TaskDetailActivity::class.java)
                intent.putExtra("task_entity", task)
                addTaskContract.launch(intent)
            }
        })

        taskAdapter.setOnCheckClickListener(object : TaskAdapter.OnCheckClickListener {
            override fun onCheckClick(task: TaskEntity, isChecked: Boolean) {
                val updatedTask = task.copy(isCompleted = isChecked)
                lifecycleScope.launch {
                    Log.e("TAG", "onCheckClick: $isChecked")
                    binding.clTask.isVisible = false
                    showProgressingView()
                    mViewModel.updateTask(updatedTask)
                    mViewModel.getAllTask()
                    taskAdapter.differ.submitList(emptyList())
                    delay(2000)
                    hideProgressingView()
                    binding.clTask.isVisible = true
                }

            }
        })
    }

    override fun setUpObserver() {
    }

    override fun disableBackground() {

    }
}