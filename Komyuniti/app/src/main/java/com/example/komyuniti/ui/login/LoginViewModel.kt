package com.example.komyuniti.ui.login

import LoginMutation
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloCall
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloMutationCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.exception.ApolloException
import com.example.komyuniti.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    suspend fun login(
        apollo: ApolloClient,
        email: String,
        password: String
    ): Response<LoginMutation.Data> {
        var res: Response<LoginMutation.Data>
        withContext(Dispatchers.IO) {
            res = loginRequest(apollo, email, password)
        }
        return res
    }

    private suspend fun loginRequest(
        apollo: ApolloClient,
        email: String,
        password: String
    ): Response<LoginMutation.Data> {
        return apollo.mutate(
            LoginMutation(
                email = email,
                password = password,
            )
        ).await()
    }
}