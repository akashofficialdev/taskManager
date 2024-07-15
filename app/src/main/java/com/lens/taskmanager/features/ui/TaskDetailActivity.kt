package com.lens.taskmanager.features.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.lens.taskmanager.R
import com.lens.taskmanager.data.local.room.TaskEntity
import com.lens.taskmanager.databinding.ActivityTaskDetailBinding
import com.lens.taskmanager.features.TaskViewModel
import com.lens.taskmanager.features.base.BaseActivity
import com.lens.taskmanager.utils.Priority
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaskDetailActivity : BaseActivity<TaskViewModel, ActivityTaskDetailBinding>(),
    OnMapReadyCallback {

    override val mViewModel: TaskViewModel by viewModel()
    private lateinit var mMap: GoogleMap
    private var latitude = 0.0
    private var longitude = 0.0
    private var mapInitialized = false

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


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        Log.e("TAG", "onMapReady: $latitude, $longitude", )
        mapInitialized = true // Set flag that map is initialized
        updateMapLocation()
    }

    private fun updateMapLocation() {
        if (mapInitialized) {
            Log.e("TAG", "onMapReady: $latitude, $longitude", )
            val location = LatLng(latitude, longitude)
            mMap.addMarker(MarkerOptions().position(location).title("Task Location"))
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
        } else {
            val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
            mapFragment?.getMapAsync(this@TaskDetailActivity)
        }
    }

    override fun initUI() {
        val task: TaskEntity? = intent.getSerializableExtra("task_entity") as? TaskEntity

        if (task != null) {
            Log.e("TAG", "initUI: $task, ${task.latitude}", )
            latitude = task.latitude
            longitude = task.longitude
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            updateMapLocation()

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


            mViewBinding.switchCompleted.setOnCheckedChangeListener { _, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                mViewModel.updateTask(updatedTask)
                setResult(Activity.RESULT_OK)
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