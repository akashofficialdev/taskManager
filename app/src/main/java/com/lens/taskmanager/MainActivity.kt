package com.lens.taskmanager

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import com.lens.taskmanager.features.ui.NavigationActivity
import com.lens.taskmanager.helper.BiometricHelper

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Handler(Looper.getMainLooper()).post {
            val biometricHelper = BiometricHelper(this)
            biometricHelper.authenticate(this, {
                // Success callback
                // Navigate to the main part of your app
                startActivity(Intent(this, NavigationActivity::class.java))
            }, {
                // Failure callback
                // Handle authentication failure
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
}