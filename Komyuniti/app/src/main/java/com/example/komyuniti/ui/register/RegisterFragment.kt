package com.example.komyuniti.ui.register

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.apollographql.apollo.api.Response
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentLoginBinding
import com.example.komyuniti.databinding.FragmentRegisterBinding
import com.example.komyuniti.models.AuthUser
import com.example.komyuniti.ui.login.LoginViewModel
import com.example.komyuniti.util.generateKeyPair
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {

    private lateinit var viewModel: RegisterViewModel
    private var fragmentRegisterBinding: FragmentRegisterBinding? = null
    private lateinit var preferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activityModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        viewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)

        // init shared preferences
        preferences = requireActivity().getSharedPreferences("Auth", Context.MODE_PRIVATE)

        val binding = FragmentRegisterBinding.inflate(inflater, container, false)

        initSignup(activityModel, viewModel, binding)

        fragmentRegisterBinding = binding
        binding.tvAlreadyHaveAccount.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }

    private fun initSignup(
        activityModel: MainViewModel,
        loginViewModel: RegisterViewModel,
        binding: FragmentRegisterBinding
    ) {
        binding.btnSignUp.setOnClickListener { view: View ->
            // get user input
            val email = binding.etEmailAddress.text.toString()
            val password = binding.etPassword.text.toString()
            val username = binding.etName.text.toString()

            // request to backend
            val apollo = activityModel.getApollo(activity as Context)
            var authUser: AuthUser?
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    authUser = loginViewModel.register(apollo, email, password, username)
                }

                // on success
                if (authUser != null) {
                    // save jwt tokens in shared preferences
                    val editor = preferences.edit()
                    editor.putString("accessToken", authUser?.token)
                    editor.putString("curUserId", authUser?.user?.id)
                    editor.apply()

                    // generate local keypair for device
                    generateKeyPair(authUser?.user?.id as String)

                    // navigation
                    Navigation.findNavController(view)
                        .navigate(R.id.action_registerFragment_to_main_navigation)
                } else {
                    Toast.makeText(activity, "Invalid email or already taken!", Toast.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentRegisterBinding = null
    }


}