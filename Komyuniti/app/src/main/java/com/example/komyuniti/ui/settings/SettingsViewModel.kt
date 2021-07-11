package com.example.komyuniti.ui.settings

import CurrentUserDetailsQuery
import UpdatePasswordMutation
import UpdateUserDetailsMutation
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.LoginInput
import type.UpdatePasswordInput
import type.UpdateUserDetailsInput

class SettingsViewModel : ViewModel() {

    suspend fun getCurrentUserDetails(
        apollo: ApolloClient
    ): User? {
        //res is
        var res: Response<CurrentUserDetailsQuery.Data>
        withContext(Dispatchers.IO) {
            res = apollo.query(CurrentUserDetailsQuery()).await()
        }
        if (res.data == null || res.data?.currentUser == null) {
            return null
        }
        return User(res.data?.currentUser?._id!!, name = res.data?.currentUser?.name, email = res.data?.currentUser?.email)
    }
     suspend fun updateCurrentUserDetails(
         apollo: ApolloClient,
         email: Input<String>,
         name: Input<String>
    ): Response<UpdateUserDetailsMutation.Data> {
         val input =
             UpdateUserDetailsInput(email, name)
         return apollo.mutate(
             UpdateUserDetailsMutation(
                 input = input
             )
         ).await()
    }
    suspend fun updatePassword(
        apollo: ApolloClient,
        old_pw: String,
        new_pw: String
    ): Response<UpdatePasswordMutation.Data> {
        val input =
            UpdatePasswordInput(old_pw, new_pw)
        return apollo.mutate(
            UpdatePasswordMutation(
                input = input
            )
        ).await()
    }

}
