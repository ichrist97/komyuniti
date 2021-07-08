package com.example.komyuniti.ui.login

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Base64
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
import com.example.komyuniti.util.generateKeyPair
import kotlinx.coroutines.*
import java.security.KeyPair
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey

class LoginFragment : Fragment() {

    private var fragmentLoginBinding: FragmentLoginBinding? = null
    private lateinit var preferences: SharedPreferences
    private lateinit var viewModel: LoginViewModel
    private lateinit var activityModel: MainViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        activityModel = ViewModelProvider(
            activity as ViewModelStoreOwner
        ).get(MainViewModel::class.java)
        viewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        // init shared preferences
        preferences = requireActivity().getSharedPreferences("Auth", Context.MODE_PRIVATE)

        val binding = FragmentLoginBinding.inflate(inflater, container, false)
        fragmentLoginBinding = binding

        // route to signup fragment
        binding.btnSignUpLogin.setOnClickListener { view: View ->
            Navigation.findNavController(view)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        initLogin(activityModel, viewModel, binding)


        lifecycleScope.launch {
            // check if already logged in
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

                    checkKeypair(authUser?.user?.id as String)

                    // navigation
                    Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_main_navigation)
                } else {
                    Toast.makeText(activity, "Invalid email or password!", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun checkKeypair(userId: String) {
        // create a new keypair for this device of no exists yet
        if(!keypairExists(userId)) {
            val keyPair = generateKeyPair(userId)
            // encode public key in base64
            val pubKeyBytes = Base64.encode(keyPair.public.encoded, Base64.DEFAULT)
            val pubKeyStr = String(pubKeyBytes)
            viewModel.addPublicKey(activityModel.getApollo(activity as Context), pubKeyStr)
        }
    }

    private fun keypairExists(userId: String): Boolean {
        val keyAlias = "keypair-$userId"
        val keyStore: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
            load(null)
        }
        return keyStore.containsAlias(keyAlias)
    }

    override fun onDestroy() {
        super.onDestroy()
        fragmentLoginBinding = null
    }

}