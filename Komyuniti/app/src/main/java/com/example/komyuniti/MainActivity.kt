package com.example.komyuniti

import LoginMutation
import SignupMutation
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.komyuniti.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // init view model
        val mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //hide top
        supportActionBar?.hide()

        /*
        val scope: CoroutineScope = MainScope()

        scope.launch {
            mainViewModel.getApollo().mutate(
                LoginMutation(
                    email = "foo@foo.de",
                    password = "123",
                )
            ).enqueue(object : ApolloCall.Callback<LoginMutation.Data>() {
                override fun onFailure(e: ApolloException) {
                    Log.e("GraphQL", e.toString(), e);
                }

                override fun onResponse(response: Response<LoginMutation.Data>) {
                    Log.i("GraphQL", response.data.toString());
                }
            }
            )
        }

         */
    }

    fun setMainNavigationController() {
        //Controller für Bottom Navigation
        val navView: BottomNavigationView = binding.navView
        //set bottom nav visible
        navView.visibility = BottomNavigationView.VISIBLE;

        val navController = findNavController(R.id.NavHostFragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_feed, R.id.navigation_events, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }
}



