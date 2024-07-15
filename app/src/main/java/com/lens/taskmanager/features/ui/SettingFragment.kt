package com.lens.taskmanager.features.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lens.taskmanager.R
import com.lens.taskmanager.databinding.FragmentSettingBinding
import com.lens.taskmanager.features.TaskViewModel
import com.lens.taskmanager.features.base.BaseFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingFragment : BaseFragment<FragmentSettingBinding, TaskViewModel>() {
    override val mViewModel: TaskViewModel by viewModel()
    override fun initUI() {
    }

    override fun setUpObserver() {
    }

    override fun disableBackground() {

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }


}
