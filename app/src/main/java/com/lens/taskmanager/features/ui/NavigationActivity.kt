package com.lens.taskmanager.features.ui

import android.os.Bundle
import com.lens.taskmanager.R
import androidx.fragment.app.Fragment
import com.lens.taskmanager.databinding.ActivityNavigationBinding
import com.lens.taskmanager.features.TaskViewModel
import com.lens.taskmanager.features.base.BaseActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class NavigationActivity :  BaseActivity<TaskViewModel, ActivityNavigationBinding>() {

    override val mViewModel: TaskViewModel by viewModel()

    override fun getViewBinding(): ActivityNavigationBinding = ActivityNavigationBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)

    }
    override fun initUI() {
        loadFragment(HomeFragment())
        mViewBinding.bottomNav.itemIconTintList = null
        mViewBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.dashboard -> {
                    loadFragment(DashboardFragment())
                    true
                }
                R.id.setting -> {
                    loadFragment(SettingFragment())
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }


}
