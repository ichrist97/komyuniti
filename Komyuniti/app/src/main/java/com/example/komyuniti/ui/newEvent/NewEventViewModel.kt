package com.example.komyuniti.ui.newEvent

import CreateEventMutation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.Event
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import type.CreateEventInput

class NewEventViewModel : ViewModel() {

    val date = MutableLiveData<String>()
    val name = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    private val komyuniti = MutableLiveData<Komyuniti?>()
    private val members = MutableLiveData<List<User>>()

    fun setKomyuniti(obj: Komyuniti?) {
        komyuniti.postValue(obj)
    }

    fun getKomyuniti(): LiveData<Komyuniti?> {
        return komyuniti
    }

    fun setMembers(obj: List<User>) {
        members.postValue(obj)
    }

    fun getMembers(): LiveData<List<User>> {
        return members
    }

    suspend fun createEvent(
        apollo: ApolloClient,
        name: String,
        date: String,
        address: String? = null,
        komyunitiId: String? = null,
        members: List<User>? = null
    ): Event? {
        val res: Response<CreateEventMutation.Data>
        withContext(Dispatchers.IO) {
            // prepare members
            val userIds = mutableListOf<String>()
            if (members != null) {
                for (member in members) {
                    userIds.add(member.id)
                }
            }

            val input = CreateEventInput(
                name = name,
                date = date,
                address = if (address != null && address.isNotEmpty()) Input.fromNullable(address) else Input.absent(),
                komyunitiId = if (komyunitiId != null) Input.fromNullable(komyunitiId) else Input.absent(),
                userIds = if (userIds.isNotEmpty()) Input.fromNullable(userIds) else Input.absent(),
            )
            res = apollo.mutate(CreateEventMutation(input = input)).await()
        }

        if (res.data == null || res.data?.createEvent == null) {
            return null
        }
        // on success
        return Event(res.data?.createEvent?._id!!)
    }

}