package com.example.komyuniti.ui.profile

import LoggedInUserQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.komyuniti.ui.profile.KomyunitiData


class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        //value = "This is notifications Fragment"
    }


    suspend fun getLoggedInUser(
        apollo: ApolloClient
    ): Response<LoggedInUserQuery.Data> {
        var res: Response<LoggedInUserQuery.Data>
        withContext(Dispatchers.IO) {
            res = apollo.query(
                LoggedInUserQuery()
            ).await()
        }
        return res
    }

    val text: LiveData<String> = _text
//  TODO: import dummy data

//    val komyunitiList = MutableLiveData() <Array<KomyunitiData>>().apply {
//        value = emptyArray()
//    }
//
//    fun checkDatabase() {
//        // Baue EventListe
//        var newList = arrayOf(KomyunitiData()) // aus backend
//        komyunitiList. = newList
//    }
}