package com.example.komyuniti

import LaunchDetailsQuery
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toDeferred
import com.apollographql.apollo.exception.ApolloException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}