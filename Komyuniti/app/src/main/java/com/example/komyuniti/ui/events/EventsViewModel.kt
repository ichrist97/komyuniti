package com.example.komyuniti.ui.events

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventsViewModel : ViewModel() {

    val eventList = MutableLiveData<Array<EventData>>().apply {
        value = emptyArray()
    }

    fun checkDatabase() {
        // Baue EventListe
        var newList = arrayOf(EventData()) // aus backend
        eventList.value = newList
    }
}