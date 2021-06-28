package com.example.komyuniti.ui.events

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventsViewModel : ViewModel() {

    val eventList = MutableLiveData<Array<EventData>>().apply {
        value = arrayOf(EventData())
    }

    fun checkDatabase() {
        // Baue EventListe
        var newList = arrayOf(EventData()) // aus backend
        eventList.value = newList
        Log.d("EventsVM", newList.toString())
    }
}