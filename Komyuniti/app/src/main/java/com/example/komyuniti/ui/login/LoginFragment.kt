package com.example.komyuniti.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
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
import androidx.viewpager2.adapter.FragmentViewHolder
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.api.Response
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentLoginBinding
import com.example.komyuniti.models.AuthUser
import com.example.komyuniti.ui.scan.ScanResultViewModel
import kotlinx.coroutines.*

class LoginFragment : Fragment() {

    private var fragmentLoginBinding: FragmentLoginBinding? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activityModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // init shared preferences
        preferences = requireActivity().getSharedPreferences("Auth", Context.MODE_PRIVATE)

        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        fragmentLoginBinding = binding
        binding.btnSignUpLogin.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        initLogin(activityModel, viewModel, binding)

        lifecycleScope.launch {
            val loggedIn =
                viewModel.checkLoginState(activityModel.getApollo(requireContext()), preferences)

            // route to profile
            if (loggedIn) {
                Navigation.findNavController(requireView())
                    .navigate(R.id.action_loginFragment_to_main_navigation)
            }
        }

        return binding.root
    }

    private fun initLogin(
        activityModel: MainViewModel,
        loginViewModel: LoginViewModel,
        binding: FragmentLoginBinding
    ) {
        binding.btnLogin.setOnClickListener { view: View ->
            // get user input
            val email = binding.etEmailLogin.text.toString()
            val password = binding.etPasswordLogin.text.toString()

            // request to backend
            val apollo = activityModel.getApollo(activity as Context)
            var authUser: AuthUser?
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    authUser = loginViewModel.login(apollo, email, password)
                }

                // on success
                if (authUser != null) {
                    // save jwt tokens in shared preferences
                    val editor = preferences.edit()
                    editor.putString("accessToken", authUser?.token)
                    editor.putString("curUserId", authUser?.user?.id)
                    editor.apply()

                    // navigation
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_main_navigation)
                } else {
                    Toast.makeText(activity, "Invalid email or password!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        fragmentLoginBinding = null
    }

}