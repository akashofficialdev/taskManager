package com.lens.taskmanager.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.lens.taskmanager.R
import com.lens.taskmanager.base.BaseFragment
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.databinding.FragmentDashboardBinding
import com.lens.taskmanager.ui.adapter.UpcomingTaskAdapter
import com.lens.taskmanager.viewmodel.TaskViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DashboardFragment : BaseFragment<FragmentDashboardBinding, TaskViewModel>() {

    override val mViewModel: TaskViewModel by viewModel()
    private lateinit var adapter: UpcomingTaskAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getAllTask()
    }

    override fun initUI() {
        setupCharts()
    }

    override fun setUpObserver() {
        adapter = UpcomingTaskAdapter()
        binding.rvTask.adapter = adapter
        binding.rvTask.layoutManager = LinearLayoutManager(requireContext())
        mViewModel.taskList.observe(viewLifecycleOwner, Observer { tasks ->
            mViewModel.fetchTaskSuggestion(tasks) { suggestion ->
                // Display the suggestion in a TextView or Dialog
                binding.progress.isVisible = false
                binding.taskSuggestion.text = suggestion
            }

            val upcomingTasks = tasks.filter { task ->
                isTaskDue(task.dueDate)
            }
            adapter.submitList(upcomingTasks)
            updateCharts(tasks)
        })



    }

    private fun isTaskDue(dueDateString: String): Boolean {
        val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val currentDate = Calendar.getInstance().time

        try {
            val dueDate = dateFormat.parse(dueDateString)
            return dueDate != null && currentDate.before(dueDate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    private fun setupCharts() {
        binding.apply {
            // Setup pie chart
            pieChart.description.isEnabled = false
            pieChart.setUsePercentValues(true)

            // Setup bar chart
            barChart.description.isEnabled = false
        }

        val customColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.coral_red_500),
            ContextCompat.getColor(requireContext(), R.color.lima_600),
            ContextCompat.getColor(requireContext(), R.color.light_blue)
            // Add more colors as needed
        )

        // Setup data entries and dataset
        val pieEntries = listOf(
            PieEntry(25f, "High Priority"),
            PieEntry(50f, "Medium Priority"),
            PieEntry(25f, "Low Priority")
            // Adjust values as per your data
        )
        val pieDataSet = PieDataSet(pieEntries, "Task Priority")
        pieDataSet.colors = customColors // Set custom colors
        val pieData = PieData(pieDataSet)

        binding.pieChart.data = pieData
        binding.pieChart.invalidate()

        binding.barChart.description.isEnabled = false


        val customBarColors = listOf(
            Color.rgb(102, 255, 178),
            Color.rgb(255, 153, 153)
        )


        val barEntries = listOf(
            BarEntry(0f, 25f, "Completed"),
            BarEntry(1f, 15f, "Pending")
        )
        val barDataSet = BarDataSet(barEntries, "Task Completion Status")
        barDataSet.colors = customBarColors // Set custom colors
        val barData = BarData(barDataSet)

        // Apply data to chart
        binding.barChart.data = barData
        binding.barChart.invalidate() // Refresh chart
    }


    private fun updateCharts(tasks: List<TaskEntity>) {
        // Update pie chart
        val priorityCounts = tasks.groupBy { it.priority }.mapValues { it.value.size }
        val pieEntries = priorityCounts.map { PieEntry(it.value.toFloat(), it.key) }
        val pieDataSet = PieDataSet(pieEntries, "Task Priority"
        )

        val customColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.coral_red_500),
            ContextCompat.getColor(requireContext(), R.color.lima_600),
            ContextCompat.getColor(requireContext(), R.color.light_blue)
            // Add more colors as needed
        )
        pieDataSet.colors = customColors
        val pieData = PieData(pieDataSet)
        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // Refresh pie chart

        // Update bar chart
        val completedCount = tasks.count { it.isCompleted }
        val pendingCount = tasks.size - completedCount

        // Create BarEntry instances with labels
        val barEntries = listOf(
            BarEntry(0f, completedCount.toFloat(), "Completed"),
            BarEntry(1f, pendingCount.toFloat(), "Pending")
        )

        // Create BarDataSet with entries
        val customBarColors = listOf(
            ContextCompat.getColor(requireContext(), R.color.lima_600),
            ContextCompat.getColor(requireContext(), R.color.coral_red_500),
            // Add more colors as needed
        )
        val barDataSet = BarDataSet(barEntries, "Task Completion Status")
        barDataSet.colors = customBarColors

        // Create BarData and set dataset
        val barData = BarData(barDataSet)
        barData.barWidth = 0.4f // Adjust bar width if needed

        // Optionally format labels as integers
        barData.setValueFormatter(WholeNumberFormatter())

        // Set data to bar chart
        binding.barChart.data = barData
        binding.barChart.invalidate() // Refresh bar chart
    }

    class WholeNumberFormatter : ValueFormatter() {
        override fun getFormattedValue(value: Float): String {
            return value.toInt().toString()
        }
    }



    override fun disableBackground() {
    }
}
