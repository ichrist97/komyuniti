package com.example.komyuniti

import LoggedInUserQuery
import android.content.Context
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import okhttp3.OkHttpClient

class MainViewModel : ViewModel() {

    private val serverUrl = BuildConfig.server_url

    fun getApollo(context: Context): ApolloClient {
        return ApolloClient.builder().serverUrl(serverUrl).okHttpClient(
            OkHttpClient.Builder()
                .addInterceptor(AuthorizationInterceptor(context))
                .build()
        ).build()
    }


}



