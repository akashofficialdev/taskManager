package com.lens.taskmanager.features.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.android.edique.edxpert.features.base.BaseViewModel
import com.lens.taskmanager.R
import com.lens.taskmanager.utils.NetworkUtils
import kotlinx.coroutines.launch

abstract class BaseActivity<VM : BaseViewModel, VB : ViewBinding> : AppCompatActivity() {

    protected lateinit var mViewBinding: VB
    protected abstract val mViewModel: VM
    private lateinit var noInternetLayout: View

    protected val activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            onActivityResultReceived(it.data)
        }
    }

    protected abstract fun getViewBinding(): VB
    protected abstract fun initUI()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewBinding = getViewBinding()
        noInternetLayout = LayoutInflater.from(this).inflate(R.layout.no_internet_layout, findViewById(android.R.id.content), false)
        noInternetLayout.findViewById<View>(R.id.btn_retry).setOnClickListener {
            onRetry()
        }

        lifecycleScope.launch {
            if (isInternetAvailable()) {
                hideNoInternetLayout()
            } else {
                showNoInternetLayout()
            }
        }

        registerBackPressCallback()
        initUI()
    }

    protected fun showNoInternetLayout() {
        if (noInternetLayout.parent == null) {
            (findViewById<ViewGroup>(android.R.id.content)).addView(noInternetLayout)
        }
        noInternetLayout.visibility = View.VISIBLE
    }

    private fun hideNoInternetLayout() {
        noInternetLayout.visibility = View.GONE
    }

    protected open fun onRetry() {
        lifecycleScope.launch {
            if (isInternetAvailable()) {
                recreate()
            } else {
                Toast.makeText(this@BaseActivity, getString(R.string.please_check_your_internet_connection), Toast.LENGTH_SHORT).show()
            }
        }
    }

    protected open fun onBackPressedCallback() {
        finish()
    }

    protected open fun onActivityResultReceived(data: Intent?) {
        // NOT NEEDED TO IMPLEMENT UNTIL AN ACTIVITY IS LAUNCHED FOR RESULT
    }

    private fun registerBackPressCallback() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                onBackPressedCallback()
            }
        }

        onBackPressedDispatcher.addCallback(this, callback)
    }

    protected suspend fun isInternetAvailable(): Boolean {
        return NetworkUtils.isInternetAvailable()
    }


    companion object {
        fun launch(
            context: Context,
            activityClass: Class<*>,
            vararg extras: Pair<String, Any> = emptyArray(),
            isClearTask: Boolean = false,
            isResultNeeded: Boolean = false
        ) {
            val intent = Intent(context, activityClass).apply {
                extras.forEach {
                    when (it.second) {
                        is Parcelable -> putExtra(it.first, it.second as Parcelable)
                        is String -> putExtra(it.first, it.second as String)
                        else -> return@forEach
                    }
                }

                if (isClearTask) {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
            }

            if (isResultNeeded) {
                (context as BaseActivity<*, *>).activityResultLauncher.launch(intent)
            } else {
                context.startActivity(intent)
            }
        }
    }
}
