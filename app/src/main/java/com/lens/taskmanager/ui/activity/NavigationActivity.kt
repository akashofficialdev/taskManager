package com.lens.taskmanager.ui.activity

import android.os.Bundle
import com.lens.taskmanager.R
import androidx.fragment.app.Fragment
import com.lens.taskmanager.databinding.ActivityNavigationBinding
import com.lens.taskmanager.viewmodel.TaskViewModel
import com.lens.taskmanager.base.BaseActivity
import com.lens.taskmanager.ui.fragment.SettingFragment
import com.lens.taskmanager.helper.LanguageChangeListener
import com.lens.taskmanager.helper.LanguageManager
import com.lens.taskmanager.ui.fragment.DashboardFragment
import com.lens.taskmanager.ui.fragment.HomeFragment
import com.lens.taskmanager.utils.CommonUtils.setLocale
import org.koin.androidx.viewmodel.ext.android.viewModel

class NavigationActivity :  BaseActivity<TaskViewModel, ActivityNavigationBinding>(),
    LanguageChangeListener {

    override val mViewModel: TaskViewModel by viewModel()

    override fun getViewBinding(): ActivityNavigationBinding = ActivityNavigationBinding.inflate(layoutInflater)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mViewBinding.root)
        applySavedLanguage()

    }

    private fun applySavedLanguage() {
        val savedLanguage = LanguageManager.getLanguage(this)
        setLocale(savedLanguage,this)
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

    override fun onLanguageChanged(language: String) {
        setLocale(language,this)
    }

}
