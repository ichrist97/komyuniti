package com.example.komyuniti.ui.editEvent

import GetEventQuery
import UpdateEventMutation
import android.util.Log
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
import type.GetEventInput
import type.UpdateEventInput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditEventViewModel : ViewModel() {
    private val members = MutableLiveData<List<User>>()

    fun setMembers(obj: List<User>) {
        members.postValue(obj)
    }

    fun getMembers(): LiveData<List<User>> {
        return members
    }

    suspend fun updateEvent(
        apollo: ApolloClient,
        id: String,
        name: String,
        date: Input<String>,
        address: String? = null,
    ): String? {
        val res: Response<UpdateEventMutation.Data>
        withContext(Dispatchers.IO) {

            val input = UpdateEventInput(
                id = id,
                name = name,
                date = date,
                address = if (address != null && address.isNotEmpty()) Input.fromNullable(address) else Input.absent(),
            )
            res = apollo.mutate(UpdateEventMutation(input = input)).await()
        }
        Log.d("updateEvent result data", res.data.toString())
        return when {
            res.data == null || res.data?.updateEvent == null -> "Couldn't save data"
            res.errors?.get(0)?.message?.isNotEmpty() == true -> res.errors?.get(0)?.message.toString()
            else -> "Updated event"
        }
    }

}