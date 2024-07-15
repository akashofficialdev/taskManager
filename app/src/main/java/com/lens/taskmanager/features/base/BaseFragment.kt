package com.lens.taskmanager.features.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.viewbinding.ViewBinding
import com.lens.taskmanager.R
import com.lens.taskmanager.utils.NetworkUtils
import kotlinx.coroutines.launch
import java.lang.reflect.ParameterizedType

abstract class BaseFragment<VB : ViewBinding, VM : ViewModel> : Fragment() {

    protected lateinit var binding: VB private set
    protected abstract val mViewModel: VM
    private var progressView: ViewGroup? = null
    protected var isProgressShowing = false

    private val type = (javaClass.genericSuperclass as ParameterizedType)
    private val classVB = type.actualTypeArguments[0] as Class<VB>
    private val classVM = type.actualTypeArguments[1] as Class<VM>

    private val inflateMethod = classVB.getMethod(
        "inflate",
        LayoutInflater::class.java,
        ViewGroup::class.java,
        Boolean::class.java
    )

    private lateinit var noInternetLayout: View

    protected abstract fun initUI()
    protected abstract fun setUpObserver()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = inflateMethod.invoke(null, inflater, container, false) as VB

        noInternetLayout = LayoutInflater.from(context).inflate(R.layout.no_internet_layout, container, false)
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

        initUI()
        setUpObserver()
        return binding.root
    }

    protected fun showProgressingView() {
        if (!isProgressShowing && view != null) {
            isProgressShowing = true
            progressView = layoutInflater.inflate(R.layout.progress_layout, null) as ViewGroup
            (view as ViewGroup).addView(progressView)
            view?.isEnabled = false
            disableBackground()
        }
    }

    protected fun hideProgressingView() {
        if (view != null && progressView != null) {
            (view as ViewGroup).removeView(progressView)
            isProgressShowing = false
            disableBackground()
        }
    }

    protected abstract fun disableBackground()

    protected fun showNoInternetLayout() {
        if (noInternetLayout.parent == null) {
            (binding.root as ViewGroup).addView(noInternetLayout)
        }
        noInternetLayout.visibility = View.VISIBLE
    }

    private fun hideNoInternetLayout() {
        noInternetLayout.visibility = View.GONE
    }

    protected suspend fun isInternetAvailable(): Boolean {
        return NetworkUtils.isInternetAvailable()
    }

    protected open fun onRetry() {
        lifecycleScope.launch {
            if (isInternetAvailable()) {
                hideNoInternetLayout()
                requireActivity().supportFragmentManager.beginTransaction().detach(this@BaseFragment).commit()
                requireActivity().supportFragmentManager.beginTransaction().attach(this@BaseFragment).commit()
            } else {
                Toast.makeText(
                    this@BaseFragment.context,
                    getString(R.string.please_check_your_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    fun checkIfFragmentAttached(operation: Context.() -> Unit) {
        if (isAdded && context != null) {
            operation(requireContext())
        }
    }

}
