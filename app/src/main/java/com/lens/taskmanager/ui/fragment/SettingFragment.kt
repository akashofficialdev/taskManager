package com.lens.taskmanager.ui.fragment

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import com.lens.taskmanager.R
import com.lens.taskmanager.databinding.FragmentSettingBinding
import com.lens.taskmanager.viewmodel.TaskViewModel
import com.lens.taskmanager.base.BaseFragment
import com.lens.taskmanager.helper.LanguageChangeListener
import com.lens.taskmanager.helper.LanguageManager
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Locale

class SettingFragment : BaseFragment<FragmentSettingBinding, TaskViewModel>(),
    LanguageChangeListener {

    override val mViewModel: TaskViewModel by viewModel()
    private var languageChangeListener: LanguageChangeListener? = null
    private var userInitiatedChange = false

    override fun initUI() {
        val languageOptions = resources.getStringArray(R.array.language_options)
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            languageOptions
        )
        binding.spinnerLanguage.adapter = adapter

        // Set the spinner selection based on the saved language
        binding.spinnerLanguage.setSelection(getLanguagePosition(LanguageManager.getLanguage(requireContext())))

        // Set the listener for user-initiated changes
        binding.spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (!userInitiatedChange) {
                    return  // Ignore programmatic changes
                }
                val languageCode = when (position) {
                    0 -> "en"
                    1 -> "hi"
                    else -> "en"
                }
                setLocale(languageCode)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }


/*
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)

        binding.switchTheme.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }
*/
    }

    private fun getLanguagePosition(language: String): Int {
        return when (language) {
            "en" -> 0
            "hi" -> 1
            else -> 0
        }
    }

    override fun setUpObserver() {
    }

    override fun disableBackground() {
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize language change listener
        if (requireActivity() is LanguageChangeListener) {
            languageChangeListener = requireActivity() as LanguageChangeListener
        } else {
            throw RuntimeException("${requireActivity()} must implement LanguageChangeListener")
        }

        // Toggle flag when fragment is created
        toggleUserInitiatedChange(true)
    }

    override fun onLanguageChanged(language: String) {
        // Handle language change if needed in the fragment
    }

    private fun setLocale(language: String) {
        // Set locale and update configuration
        Toast.makeText(
            requireContext(),
            getString(R.string.language_change_text),
            Toast.LENGTH_SHORT
        ).show()
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = Configuration()
        config.locale = locale
        requireContext().resources.updateConfiguration(
            config,
            requireContext().resources.displayMetrics
        )

        // Save selected language to SharedPreferences
        LanguageManager.saveLanguage(language, requireContext())

        // Notify activity about language change
        languageChangeListener?.onLanguageChanged(language)
    }

    override fun onDetach() {
        super.onDetach()
        languageChangeListener = null
    }

    override fun onDestroyView() {
        super.onDestroyView()

        // Reset flag when fragment is destroyed
        toggleUserInitiatedChange(false)
    }

    private fun toggleUserInitiatedChange(enabled: Boolean) {
        userInitiatedChange = enabled
    }
}
