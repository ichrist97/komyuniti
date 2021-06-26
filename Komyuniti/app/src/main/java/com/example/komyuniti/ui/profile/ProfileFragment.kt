package com.example.komyuniti.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.example.komyuniti.MainActivity
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentProfileBinding
import com.example.komyuniti.ui.login.LoginViewModel
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private lateinit var profileViewModel: ProfileViewModel
    private var _binding: FragmentProfileBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val activityModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        // init shared preferences
        preferences = activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!

        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        lifecycleScope.launch {
            val res = profileViewModel.getLoggedInUser(activityModel.getApollo(activity as Context))
            Log.d("TEST", res.data?.loggedInUser?.id!!)
        }

        initLogout(binding)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initLogout(binding: FragmentProfileBinding) {
        binding.profileLogoutBtn.setOnClickListener { view: View ->
            //TODO: connection to backend quit user session
            Navigation.findNavController(view)
                .navigate(R.id.action_navigation_profile_to_loginFragment)
        }
    }
}