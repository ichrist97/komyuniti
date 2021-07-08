package com.example.komyuniti.ui.login

import AddPublicKeyMutation
import LoggedInUserQuery
import LoginMutation
import android.content.Context
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
import com.example.komyuniti.MainActivity
import com.example.komyuniti.MainViewModel
import com.example.komyuniti.models.AuthUser
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import type.LoginInput

class LoginViewModel : ViewModel() {

    suspend fun login(
        apollo: ApolloClient,
        email: String,
        password: String
    ): AuthUser? {
        var res: Response<LoginMutation.Data>
        withContext(Dispatchers.IO) {
            res = loginRequest(apollo, email, password)
        }

        if (res.data == null || res.data?.login == null) {
            return null
        }

        val user = User(res.data?.login?.user?._id!!, res.data?.login?.user?.name)
        return AuthUser(res.data?.login?.token!!, user)
    }

    private suspend fun loginRequest(
        apollo: ApolloClient,
        email: String,
        password: String
    ): Response<LoginMutation.Data> {
        val input = LoginInput(email, password)
        return apollo.mutate(
            LoginMutation(
                input = input
            )
        ).await()
    }

    suspend fun checkLoginState(apollo: ApolloClient, preferences: SharedPreferences): Boolean {
        /*
        Search for a saved token in shared preferences. If found then check if the token is still valid.
        If token is valid then directly naviate to profile. If no token is found or it is invalid, then
        route to login view
         */
        // init shared preferences
        val token = preferences.getString("accessToken", null)

        // was token found
        if (token == null) {
            return false
        }

        // check if token is still valid
        val res = apollo.query(LoggedInUserQuery()).await()

        if (res.data == null || res.data?.currentUser == null) {
            return false
        }
        return true
    }

    fun addPublicKey(apollo: ApolloClient, pubKey: String){
        viewModelScope.launch {
            val res = apollo.mutate(AddPublicKeyMutation(key=pubKey)).await()
            if (res.data == null || res.data?.addPublicKey == null) {
                Log.d("LoginViewModel", "Failed to add new public key")
            }
        }
    }
}