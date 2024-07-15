package com.lens.taskmanager.features.ui

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.lens.taskmanager.R
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.databinding.FragmentDashboardBinding
import com.lens.taskmanager.features.TaskViewModel
import com.lens.taskmanager.features.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class DashboardFragment : BaseFragment<FragmentDashboardBinding, TaskViewModel>() {

    override val mViewModel: TaskViewModel by viewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mViewModel.getAllTask()
    }

    override fun initUI() {
        setupCharts()
    }

    override fun setUpObserver() {
        // Observe task list changes
        mViewModel.taskList.observe(viewLifecycleOwner, Observer { tasks ->
            // Update UI based on task list changes
            updateCharts(tasks)
        })
    }

    private fun setupCharts() {
        binding.apply {
            // Setup pie chart
            pieChart.description.isEnabled = false // Disable description
            pieChart.setUsePercentValues(true) // Use percentage values

            // Setup bar chart
            barChart.description.isEnabled = false // Disable description
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

        // Apply data to chart
        binding.pieChart.data = pieData
        binding.pieChart.invalidate() // Refresh chart

        // Setup bar chart
        binding.barChart.description.isEnabled = false // Disable description

        // Define your custom colors for bar chart
        val customBarColors = listOf(
            Color.rgb(102, 255, 178), // Green
            Color.rgb(255, 153, 153)  // Light Red
            // Add more colors as needed
        )

        // Setup data entries and dataset for bar chart
        val barEntries = listOf(
            BarEntry(0f, 25f,"Completed"), // Completed tasks
            BarEntry(1f, 15f,"Pending")  // Pending tasks
            // Adjust values as per your data
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
