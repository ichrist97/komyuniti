package com.example.komyuniti

import LaunchDetailsQuery
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.apollographql.apollo.ApolloClient
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

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //hide top
        supportActionBar?.hide()
        val apolloClient = ApolloClient.builder()
            .serverUrl("https://apollo-fullstack-tutorial.herokuapp.com/graphql").build()

        val scope: CoroutineScope = MainScope()


        scope.launch {
            val response = try {
                apolloClient.query(LaunchDetailsQuery(id = "83")).await()
            } catch (e: ApolloException) {
                // handle protocol errors
                return@launch
            }

            val launch = response.data?.launch
            if (launch == null || response.hasErrors()) {
                // handle application errors
                return@launch
            }

            // launch now contains a typesafe model of your data
            Log.d("MainActivity","Launch site: ${launch.site}")
        }


    }

    fun setMainNavigationController() {
        //Controller für Bottom Navigation
        val navView: BottomNavigationView = binding.navView
        //set bottom nav visible
        navView.setVisibility(BottomNavigationView.VISIBLE);

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