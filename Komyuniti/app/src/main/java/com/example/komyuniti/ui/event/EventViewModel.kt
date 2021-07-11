package com.example.komyuniti.ui.event

import DeleteEventMutation
import GetEventQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.Event
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import type.DeleteEventInput
import type.GetEventInput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EventViewModel : ViewModel() {

    private val event = MutableLiveData<Event?>(null)
    private val eventId = MutableLiveData<String>()

    fun getEventId(): LiveData<String> {
        return eventId
    }

    fun setEventId(id: String) {
        eventId.postValue(id)
    }

    fun getEvent(): LiveData<Event?> {
        return event
    }

    fun fetchEvent(apollo: ApolloClient, eventId: String) {
        viewModelScope.launch {
            val input = GetEventInput(id = eventId)
            val res = apollo.query(GetEventQuery(input = input)).await()

            if (res.data == null || res.data?.event == null) {
                event.postValue(null)
            }

            // wrap into data class
            var komyuniti: Komyuniti? = null
            if (res.data?.event?.komyuniti?._id != null) {
                komyuniti = Komyuniti(
                    res.data?.event?.komyuniti?._id!!,
                    name = res.data?.event?.komyuniti?.name
                )
            }

            // members
            val members = mutableListOf<User>()
            for (obj in res.data?.event?.members!!) {
                val user = User(obj._id, name = obj.name)
                members.add(user)
            }

            // parse date
            val dateStr = res.data?.event?.date?.split(" ")!![0]
            val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)

            val _event = Event(
                res.data?.event?._id!!,
                name = res.data?.event?.name,
                date = date,
                komyuniti = komyuniti,
                members = members,
                address = res.data?.event?.address
            )
            event.postValue(_event)
        }
    }

    suspend fun deleteEvent(apollo: ApolloClient, eventId: String): Boolean {
        val res: Response<DeleteEventMutation.Data>
        withContext(Dispatchers.IO) {
            val input = DeleteEventInput(id=eventId)
            res = apollo.mutate(DeleteEventMutation(input=input)).await()
        }

        if(res.data == null || res.data?.deleteEvent == null) {
            return false
        }
        // on success
        return true
    }
}