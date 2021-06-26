package com.example.komyuniti.ui.register

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import com.example.komyuniti.ui.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterFragment : Fragment() {

    private lateinit var registerViewModel: RegisterViewModel
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
        val model: RegisterViewModel by viewModels()

        // init shared preferences
        preferences = this.activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!

        val binding = FragmentRegisterBinding.inflate(inflater, container, false)

        initSignup(activityModel, model, binding)

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
            var res: Response<SignupMutation.Data>
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    res = loginViewModel.signup(apollo, email, password, username)
                }

                // on success
                if (res.data != null && res.errors == null) {
                    // save jwt tokens in shared preferences
                    val editor = preferences.edit()
                    editor.putString("accessToken", res.data?.signup?.accessToken)
                    editor.putString("refreshToken", res.data?.signup?.refreshToken)
                    editor.commit()

                    // navigation
                    Navigation.findNavController(view)
                        .navigate(R.id.action_registerFragment_to_mobile_navigation)
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