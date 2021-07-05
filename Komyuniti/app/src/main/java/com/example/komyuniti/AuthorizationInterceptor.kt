package com.example.komyuniti

import android.content.Context
import android.content.SharedPreferences
import okhttp3.Interceptor
import okhttp3.Response

class AuthorizationInterceptor(val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val preferences: SharedPreferences =
            context.getSharedPreferences("Auth", Context.MODE_PRIVATE)
        val accessToken = preferences.getString("accessToken", null)

        if (accessToken != null) {
            val request = chain.request().newBuilder()
                .addHeader("Authorization", accessToken)
                .build()
            return chain.proceed(request)
        }
        return chain.proceed(chain.request())
    }
}