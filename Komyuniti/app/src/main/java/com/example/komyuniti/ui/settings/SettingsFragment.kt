
package com.example.komyuniti.ui.settings

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentProfileBinding
import com.example.komyuniti.databinding.FragmentRegisterBinding
import com.example.komyuniti.databinding.FragmentSettingsBinding
//import com.example.komyuniti.ui.feed.SettingsViewModel

class SettingsFragment : Fragment() {

    private lateinit var settingsViewModel: SettingsViewModel
    private lateinit var fragmentSettingsBinding : FragmentSettingsBinding

    // This property is only valid between onCreateView and
    // onDestroyView.


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        settingsViewModel =
            ViewModelProvider(this).get(SettingsViewModel::class.java)

        fragmentSettingsBinding = FragmentSettingsBinding.inflate(inflater, container, false)

        val root: View = fragmentSettingsBinding.root

        initProfile(fragmentSettingsBinding)
        return root
    }

    private fun initProfile(binding: FragmentSettingsBinding) {
        binding.tvBack.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_settingsFragment_to_navigation_profile2)
        }
    }
}
