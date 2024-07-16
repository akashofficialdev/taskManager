package com.lens.taskmanager.ui.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.lens.taskmanager.R
import com.lens.taskmanager.databinding.ActivityMainBinding
import com.lens.taskmanager.viewmodel.TaskViewModel
import com.lens.taskmanager.base.BaseActivity
import com.lens.taskmanager.helper.BiometricHelper
import com.lens.taskmanager.helper.LanguageManager
import com.lens.taskmanager.utils.CommonUtils.setLocale
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity<TaskViewModel, ActivityMainBinding>() {
    override val mViewModel: TaskViewModel by viewModel()

    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val savedLanguage = LanguageManager.getLanguage(this)
        Log.e("TAG", "onCreate: $savedLanguage")
        setLocale(savedLanguage, this)

        Handler(Looper.getMainLooper()).post {
            val biometricHelper = BiometricHelper(this)
            biometricHelper.authenticate(this, {
                val progressBar = this.findViewById<ProgressBar>(R.id.progress_bar)
                progressBar.isVisible = true
                startActivity(Intent(this, NavigationActivity::class.java))
            }, {
                showAuthenticationError()
            })
        }


    }

    private fun showAuthentication() {
        val biometricHelper = BiometricHelper(this)
        biometricHelper.authenticate(this, {
            startActivity(Intent(this, NavigationActivity::class.java))
        }, {
            showAuthenticationError()
        })
    }

    private fun showAuthenticationError() {
        val text = this.findViewById<TextView>(R.id.tv_failed)
        val btnRetry = this.findViewById<Button>(R.id.btn_retry)
        text.isVisible = true
        btnRetry.isVisible = true
        btnRetry.setOnClickListener {
            showAuthentication()
        }
    }

    override fun initUI() {
    }
}