package com.example.komyuniti.ui.register

import SignupMutation
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class RegisterViewModel: ViewModel() {

    suspend fun signup(
        apollo: ApolloClient,
        email: String,
        password: String,
        username: String
    ): Response<SignupMutation.Data> {
        var res: Response<SignupMutation.Data>
        withContext(Dispatchers.IO) {
            res = signupRequest(apollo, email, password, username)
        }
        return res
    }

    private suspend fun signupRequest(
        apollo: ApolloClient,
        email: String,
        password: String,
        username: String
    ): Response<SignupMutation.Data> {
        return apollo.mutate(
            SignupMutation(
                email = email,
                password = password,
                name = username
            )
        ).await()
    }
}