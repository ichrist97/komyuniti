package com.example.komyuniti.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.komyuniti.MainActivity
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private var fragmentLoginBinding : FragmentLoginBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        fragmentLoginBinding = binding
        binding.btnSignUpLogin.setOnClickListener{ view : View ->
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.btnLogin.setOnClickListener{ view : View ->
            //TODO: connection to backend get user
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_mobile_navigation)
            (activity as MainActivity).setMainNavigationController()

        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentLoginBinding = null
    }

}