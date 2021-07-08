package com.example.komyuniti.ui.register

import RegisterMutation
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.AuthUser
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.RegisterInput
import type.Role
import java.security.KeyPair
import java.security.KeyPairGenerator

class RegisterViewModel : ViewModel() {


    suspend fun register(
        apollo: ApolloClient,
        email: String,
        password: String,
        username: String
    ): AuthUser? {
        var res: Response<RegisterMutation.Data>
        withContext(Dispatchers.IO) {
            res = registerRequest(apollo, email, password, username)
        }

        if (res.data == null || res.data?.register == null) {
            return null
        }

        val user = User(res.data?.register?.user?._id!!, res.data?.register?.user?.name)
        return AuthUser(res.data?.register?.token!!, user)
    }

    private suspend fun registerRequest(
        apollo: ApolloClient,
        email: String,
        password: String,
        username: String
    ): Response<RegisterMutation.Data> {
        // workaround for adress
        val address = Input.fromNullable("Hirschgarten 1")
        val input = RegisterInput(username, email, password, Role.MEMBER, address)
        return apollo.mutate(
            RegisterMutation(
                input = input
            )
        ).await()
    }
}