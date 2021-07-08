package com.example.komyuniti.ui.events

import GetEventQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.Event
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.models.User
import kotlinx.coroutines.launch
import type.GetEventInput

class DoneEventViewModel : ViewModel() {

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
            val komyuniti = Komyuniti(
                res.data?.event?.komyuniti?._id!!,
                name = res.data?.event?.komyuniti?.name
            )

            // members
            val members = mutableListOf<User>()
            for (obj in res.data?.event?.members!!) {
                val user = User(obj._id, name = obj.name)
                members.add(user)
            }

            val _event = Event(
                res.data?.event?._id!!,
                name = res.data?.event?.name,
                date = res.data?.event?.date,
                komyuniti = komyuniti,
                members = members,
                address = res.data?.event?.address
            )
            event.postValue(_event)
        }
    }
}