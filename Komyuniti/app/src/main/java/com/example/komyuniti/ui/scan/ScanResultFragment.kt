package com.example.komyuniti.ui.scan

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentScanBinding
import com.example.komyuniti.databinding.FragmentScanResultBinding
import com.example.komyuniti.ui.profile.ProfileViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class ScanResultFragment : Fragment() {

    private var _binding: FragmentScanResultBinding? = null
    private lateinit var viewModel: ScanResultViewModel
    private lateinit var activityViewModel: MainViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activityViewModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        viewModel = ViewModelProvider(requireActivity()).get(ScanResultViewModel::class.java)
        _binding = FragmentScanResultBinding.inflate(inflater, container, false)
        val root: View = binding.root

        initUserInfo()
        initAddFriend()
        initGoBack()

        return root
    }

    private fun initGoBack() {
        // go back to profile
        binding.btnCloseNewFriend.setOnClickListener {
            Navigation.findNavController(it)
                .navigate(R.id.action_scanResultFragment_to_navigation_profile)
        }
    }

    private fun initUserInfo() {
        // DEV WORKAROUND
        /*
        viewModel.setQR(
            "eyJpZCI6IjYwZTFkNjk3ZDYzOGQ1MTc3YzE2YzUwMiIsInB1YmxpY0tleSI6Ik1Ga3dFd1lIS29aSXpqMENBUVlJS29aSXpqMERBUWNEUWdBRWxRTFF1aVQ0dUdxcXBRczM0M3FLbllCeC9pck8vOFordGQyMkQrenVcbmNXYXljQWJuTWJmeXgzOXpuVVM4RW80RXd6TWYyNmxsblNQc3JsZmtDSlRub3dcdTAwM2RcdTAwM2RcbiJ9"
        )
        */
        viewModel.getQR().observe(viewLifecycleOwner, Observer {
            lifecycleScope.launch {
                val user = viewModel.fetchUser(activityViewModel.getApollo(requireContext()))

                // error handling
                if (user == null) {
                    Toast.makeText(activity, "User could not be found!", Toast.LENGTH_LONG).show()
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_scanResultFragment_to_navigation_profile)
                } else {
                    binding.newFriendName.text = user.name
                }
            }
        })
    }

    private fun initAddFriend() {
        binding.btnAddFriend.setOnClickListener {
            lifecycleScope.launch {
                val user = viewModel.addFriend(activityViewModel.getApollo(requireContext()))

                // error handling
                if (user == null) {
                    Toast.makeText(activity, "Could not add as friend!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "Friend added", Toast.LENGTH_LONG).show()
                    Navigation.findNavController(requireView())
                        .navigate(R.id.action_scanResultFragment_to_navigation_profile)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}