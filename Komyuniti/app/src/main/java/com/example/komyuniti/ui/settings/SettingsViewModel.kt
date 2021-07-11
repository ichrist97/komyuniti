package com.example.komyuniti.ui.settings

import CurrentUserDetailsQuery
import UpdateUserDetailsMutation
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.LoginInput
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

}
