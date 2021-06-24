package com.example.komyuniti.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.komyuniti.ui.profile.KomyunitiData


class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        //value = "This is notifications Fragment"
    }
    val text: LiveData<String> = _text
//  TODO: import dummy data

//    val komyunitiList = MutableLiveData() <Array<KomyunitiData>>().apply {
//        value = emptyArray()
//    }
//
//    fun checkDatabase() {
//        // Baue EventListe
//        var newList = arrayOf(KomyunitiData()) // aus backend
//        komyunitiList. = newList
//    }
}