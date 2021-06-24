package com.example.komyuniti.ui.events

import android.util.EventLog
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class EventsViewModel : ViewModel() {

    public val eventList = MutableLiveData<Array<EventData>>().apply {
        value = emptyArray()
    }

    public fun checkDatabase(){
        // Baue EventListe
        var newList = arrayOf(EventData()) // aus backend
        eventList.value = newList
    }
}