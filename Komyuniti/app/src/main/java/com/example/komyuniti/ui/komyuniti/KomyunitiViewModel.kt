package com.example.komyuniti.ui.komyuniti

import DeleteKomyunitiMutation
import GetEventsByKomyunitiQuery
import GetKomyunitiQuery
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
import type.DeleteKomyunitiInput
import type.GetKomyunitiInput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class KomyunitiViewModel : ViewModel() {

    private val komyuniti = MutableLiveData<Komyuniti?>(null)
    private val events = MutableLiveData<List<Event>?>(null)
    private val upcomingEvents = MutableLiveData<List<Event>?>(null)
    private val doneEvents = MutableLiveData<List<Event>?>(null)
    private val komyunitiId = MutableLiveData<String>()

    fun getEvents(): LiveData<List<Event>?> {
        return events
    }

    fun getUpcomingEvents(): LiveData<List<Event>?> {
        return upcomingEvents
    }

    fun getDoneEvents(): LiveData<List<Event>?> {
        return doneEvents
    }

    fun setKomyuniti(id: String) {
        komyunitiId.postValue(id)
    }

    fun getKomyunitiId(): LiveData<String> {
        return komyunitiId
    }

    fun getKomyuniti(): LiveData<Komyuniti?> {
        return komyuniti
    }

    private fun filterEvents(events: List<Event>?) {
        val upcoming = filterUpcomingEvents(events)
        upcomingEvents.postValue(upcoming)

        val done = filterDoneEvents(events)
        doneEvents.postValue(done)
    }

    private fun filterUpcomingEvents(events: List<Event>?): List<Event> {
        // return empty list
        if (events == null) {
            return listOf()
        }

        val upcoming = mutableListOf<Event>()
        val now = LocalDate.now()

        for (event in events) {
            // compare now and eventDate
            if (event.date!! >= now) {
                upcoming.add(event)
            }
        }
        return upcoming
    }

    private fun filterDoneEvents(events: List<Event>?): List<Event> {
        // return empty list
        if (events == null) {
            return listOf()
        }

        val done = mutableListOf<Event>()
        val now = LocalDate.now()

        for (event in events) {
            // compare now and eventDate
            if (event.date!! < now) {
                done.add(event)
            }
        }
        return done
    }

    suspend fun fetchKomyuniti(apollo: ApolloClient, komyunitiId: String) {
        withContext(Dispatchers.IO) {
            val input = GetKomyunitiInput(komyunitiId)
            val res = apollo.query(GetKomyunitiQuery(input = input)).await()

            if (res.data == null || res.data?.komyuniti == null) {
                komyuniti.postValue(null)
            }

            // wrap into data class
            val members = mutableListOf<User>()
            for (obj in res.data?.komyuniti?.members!!) {
                val user = User(obj._id, name = obj.name)
                members.add(user)
                val admin = User(res.data?.komyuniti?.admin?._id!!)

                // parse date
                val dateStr =
                    res.data?.komyuniti?.createdAt?.split(" ")!![0] // split at whitespace for deleting the minutes and take only the date part
                val date =
                    LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)

                val _komyuniti = Komyuniti(
                    res.data?.komyuniti?._id!!,
                    name = res.data?.komyuniti?.name,
                    members = members,
                    admin = admin,
                    createdAt = date
                )
                komyuniti.postValue(_komyuniti)
            }
        }
    }

    suspend fun fetchEvents(apollo: ApolloClient, komyunitiId: String) {
        // need to await the result
        withContext(Dispatchers.IO) {
            val res =
                apollo.query(
                    GetEventsByKomyunitiQuery(
                        komyunitiId = Input.fromNullable(
                            komyunitiId
                        )
                    )
                )
                    .await()
            if (res.data == null || res.data?.events == null) {
                events.postValue(null)
            }

            // wrap into data class
            val _events = mutableListOf<Event>()
            for (obj in res.data?.events!!) {

                // parse date
                val dateStr =
                    obj!!.date?.split(" ")!![0] // split at whitespace for deleting the minutes and take only the date part
                val date =
                    LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
                val event = Event(
                    obj._id,
                    name = obj.name,
                    date = date,
                )
                _events.add(event)
            }
            filterEvents(_events)
            events.postValue(_events)
        }
    }

    suspend fun deleteKomyuniti(apollo: ApolloClient, komyunitiId: String): Boolean {
        val res: Response<DeleteKomyunitiMutation.Data>
        withContext(Dispatchers.IO) {
            val input = DeleteKomyunitiInput(id = komyunitiId)
            res = apollo.mutate(DeleteKomyunitiMutation(input = input)).await()
        }

        if(res.data == null || res.data?.deleteKomyuniti == null) {
            return false
        }
        return true
    }
}

