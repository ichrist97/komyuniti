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
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.api.Response
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.R
import com.example.komyuniti.databinding.FragmentLoginBinding
import kotlinx.coroutines.*

class LoginFragment : Fragment() {

    private var fragmentLoginBinding: FragmentLoginBinding? = null
    private lateinit var preferences: SharedPreferences


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val activityModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        val model: LoginViewModel by viewModels()

        // init shared preferences
        preferences = this.activity?.getSharedPreferences("Auth", Context.MODE_PRIVATE)!!

        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        fragmentLoginBinding = binding
        binding.btnSignUpLogin.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        initLogin(activityModel, model, binding)

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
            var res: Response<LoginMutation.Data>
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    res = loginViewModel.login(apollo, email, password)
                }

                // on success
                if (res.data != null && res.errors == null) {
                    // save jwt tokens in shared preferences
                    val editor = preferences.edit()
                    editor.putString("accessToken", res.data?.login?.accessToken)
                    editor.putString("refreshToken", res.data?.login?.refreshToken)
                    editor.commit()

                    // navigation
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_mobile_navigation)
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