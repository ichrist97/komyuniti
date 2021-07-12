package com.example.komyuniti.ui.editEvent

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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class EditEventViewModel : ViewModel() {
    private val event = MutableLiveData<Event?>(null)
    private val eventId = MutableLiveData<String>()
    private val members = MutableLiveData<List<User>>()
    private val komyuniti = MutableLiveData<Komyuniti>()



    fun getEventId(): LiveData<String> {
        return eventId
    }

    fun setEventId(id: String) {
        eventId.postValue(id)
    }

    fun getEvent(): LiveData<Event?> {
        return event
    }

    fun setMembers(obj: List<User>) {
        members.postValue(obj)
    }

    fun getMembers(): LiveData<List<User>> {
        return members
    }

    fun setKomyuniti(obj: Komyuniti) {
        komyuniti.postValue(obj)
    }

    fun getKomyuniti(): LiveData<Komyuniti> {
        return komyuniti
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
}