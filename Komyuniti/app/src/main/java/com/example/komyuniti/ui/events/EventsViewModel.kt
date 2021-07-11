package com.example.komyuniti.ui.events

import GetEventsQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.Event
import com.example.komyuniti.models.Komyuniti
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.*
import java.time.format.DateTimeFormatter

class EventsViewModel : ViewModel() {

    private val events = MutableLiveData<List<Event>?>(null)
    private val upcomingEvents = MutableLiveData<List<Event>?>(null)
    private val doneEvents = MutableLiveData<List<Event>?>(null)

    fun getEvents(): LiveData<List<Event>?> {
        return events
    }

    fun getUpcomingEvents(): LiveData<List<Event>?> {
        return upcomingEvents
    }

    fun getDoneEvents(): LiveData<List<Event>?> {
        return doneEvents
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

    suspend fun fetchEvents(apollo: ApolloClient, userId: String) {
        // need to await the result
        withContext(Dispatchers.IO) {
            val res = apollo.query(GetEventsQuery(userId = Input.fromNullable(userId))).await()
            if (res.data == null || res.data?.events == null) {
                events.postValue(null)
            }

            // wrap into data class
            val _events = mutableListOf<Event>()
            for (obj in res.data?.events!!) {

                var komyuniti: Komyuniti? = null
                if (obj?.komyuniti?._id != null) {
                    komyuniti = Komyuniti(obj.komyuniti._id, name = obj.komyuniti.name)
                }

                // parse date
                val dateStr =
                    obj!!.date?.split(" ")!![0] // split at whitespace for deleting the minutes and take only the date part
                val date =
                    LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
                val createdStr =
                    obj.createdAt?.split(" ")!![0]
                val createdAt = LocalDate.parse(createdStr, DateTimeFormatter.ISO_DATE)
                val event = Event(
                    obj._id,
                    name = obj.name,
                    createdAt = createdAt,
                    date = date,
                    komyuniti = komyuniti
                )
                _events.add(event)
            }
            filterEvents(_events)
            events.postValue(_events)
        }
    }
}