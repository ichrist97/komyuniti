package com.example.komyuniti.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentRegisterBinding


class RegisterFragment : Fragment() {

    private var fragmentRegisterBinding: FragmentRegisterBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val binding = FragmentRegisterBinding.inflate(inflater, container,false)

        fragmentRegisterBinding = binding
        binding.tvAlreadyHaveAccount.setOnClickListener{ view: View ->
            Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment)
        }
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentRegisterBinding = null
    }
}