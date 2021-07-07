package com.example.komyuniti

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.subscription.SubscriptionConnectionParams
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport
import okhttp3.OkHttpClient
import java.lang.Exception

class MainViewModel : ViewModel() {

    private val serverUrl = BuildConfig.server_url

    fun getApollo(context: Context): ApolloClient {
        // setup server url
        val subscriptionUrl: String
        if (serverUrl.contains("http://")) {
            subscriptionUrl = serverUrl.replace("http://", "ws://")
        } else if (serverUrl.contains("https://")) {
            subscriptionUrl = serverUrl.replace("https://", "wss://")
        } else {
            throw IllegalArgumentException("Invalid protocol")
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthorizationInterceptor(context))
            .build()

        // conection parameters for subscription to work
        // see: https://github.com/apollographql/apollo-android/issues/1864#issuecomment-744722211
        val preferences: SharedPreferences =
            context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val accessToken = preferences.getString("accessToken", null)
            ?: throw Exception("No access token set")

        val connectionParams: MutableMap<String, String> = HashMap()
        connectionParams["Authorization"] = accessToken

        return ApolloClient.builder()
            .serverUrl(serverUrl)
            .subscriptionConnectionParams(SubscriptionConnectionParams(connectionParams))
            .subscriptionTransportFactory(
                WebSocketSubscriptionTransport.Factory(
                    subscriptionUrl,
                    okHttpClient
                )
            )
            .okHttpClient(okHttpClient)
            .build()
    }


}



