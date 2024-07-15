package com.lens.taskmanager.features.ui

import android.app.Activity
import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Toast
import com.lens.taskmanager.R
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.databinding.ActivityCreateTaskBinding
import com.lens.taskmanager.features.TaskViewModel
import com.lens.taskmanager.features.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CreateTaskActivity : BaseActivity<TaskViewModel, ActivityCreateTaskBinding>() {
    private var isEdit = false
    private var id = 0

    override val mViewModel: TaskViewModel by viewModel()

    override fun getViewBinding(): ActivityCreateTaskBinding =
        ActivityCreateTaskBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

    }

    override fun initUI() {
        val task: TaskEntity? = intent.getSerializableExtra("task_entity") as? TaskEntity

        if (task != null) {
            isEdit = true
            mViewBinding.apply {
                id = task.id
                toolbarTitle.text = getString(R.string.edit_task_title)
                title.setText(task.title)
                description.setText(task.description)
                dueDate.text = task.dueDate

                ArrayAdapter.createFromResource(
                    this@CreateTaskActivity,
                    R.array.priority_levels,
                    android.R.layout.simple_spinner_item
                ).also { adapter ->
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                    spinnerPriority.adapter = adapter

                    // Find the position of the task's priority in the array
                    val priorityPosition = adapter.getPosition(task.priority)
                    spinnerPriority.setSelection(priorityPosition)
                }

            }
        }else{
            ArrayAdapter.createFromResource(
                this,
                R.array.priority_levels,
                android.R.layout.simple_spinner_item
            ).also { adapter ->
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                mViewBinding.spinnerPriority.adapter = adapter
            }
        }
        mViewBinding.cvDueDate.setOnClickListener {
            showDatePickerDialog(mViewBinding.dueDate)
        }
        mViewBinding.btnSave.setOnClickListener {
            validateAndSave()
        }


    }

    private fun validateAndSave() {
        var isValid = true
        if (mViewBinding.title.text.isEmpty() || mViewBinding.title.text.equals(getString(R.string.select_date))) {
            mViewBinding.title.error = "Please Enter title"
            isValid = false
        } else {
            mViewBinding.title.error = null
        }

        if (mViewBinding.description.text.isEmpty() || mViewBinding.description.text.equals(
                getString(R.string.select_date)
            )
        ) {
            mViewBinding.description.error = "Please Enter description"
            isValid = false
        } else {
            mViewBinding.description.error = null
        }

        if (mViewBinding.dueDate.text.isEmpty() || mViewBinding.dueDate.text.equals(getString(R.string.select_date))) {
            mViewBinding.dueDate.error = "Please Enter dueDate"
            isValid = false
        } else {
            mViewBinding.dueDate.error = null
        }

        val selectedPosition = mViewBinding.spinnerPriority.selectedItemPosition
        if (selectedPosition == AdapterView.INVALID_POSITION) {
            Toast.makeText(this, "Please select a priority", Toast.LENGTH_SHORT).show()
            isValid = false
        }

        if (isValid) {
            val title = mViewBinding.title.text.toString()
            val description = mViewBinding.description.text.toString()
            val dueDate = mViewBinding.dueDate.text.toString()
            val priority = mViewBinding.spinnerPriority.selectedItem.toString()
            val latitude = 25.343534 // Replace with actual latitude
            val longitude = 72.232343
            if (isEdit) {
                val task = TaskEntity(
                    id = id,
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority,
                    isCompleted = false,
                    latitude = latitude,
                    longitude = longitude
                )
                mViewModel.updateTask(task)
            } else {
                val task = TaskEntity(
                    title = title,
                    description = description,
                    dueDate = dueDate,
                    priority = priority,
                    isCompleted = false,
                    latitude = latitude,
                    longitude = longitude
                )
                mViewModel.insertTask(task)
            }

            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun showDatePickerDialog(dateTextView: TextView) {
        val calendar = Calendar.getInstance()

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                // Format date as yyyy-MM-dd
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)
                val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                val dateString = dateFormat.format(selectedDate.time)

                // Set the date to the TextView
                dateTextView.text = dateString
                dateTextView.setTextColor(Color.BLACK)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000

        val endOfYear = Calendar.getInstance()
        endOfYear.add(Calendar.YEAR, 1)
        endOfYear.set(Calendar.MONTH, Calendar.DECEMBER)
        endOfYear.set(Calendar.DAY_OF_MONTH, 31)
        datePickerDialog.datePicker.maxDate = endOfYear.timeInMillis

        datePickerDialog.show()
    }

}
