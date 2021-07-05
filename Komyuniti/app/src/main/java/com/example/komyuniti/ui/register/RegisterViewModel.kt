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

    fun generateKeyPair(): KeyPair {
        /*
        * Generate a new EC key pair entry in the Android Keystore by
        * using the KeyPairGenerator API. The private key can only be
        * used for signing or verification and only with SHA-256 or
        * SHA-512 as the message digest.
        */
        val keyStoreProvider = "AndroidKeyStore"
        val keyAlias = "UserKey"
        val generator: KeyPairGenerator = KeyPairGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_EC,
            keyStoreProvider
        )
        val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
            keyAlias,
            KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
        ).run {
            setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
            build()
        }
        generator.initialize(parameterSpec)

        // generate and save key pair in key store by its alias
        return generator.genKeyPair()
    }
}