package com.example.komyuniti.ui.newEvent.addKomyuniti

import GetKomunitiesOverviewQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.models.User
import kotlinx.coroutines.launch

class AddKomyunitiViewModel : ViewModel() {

    private val komyunities = MutableLiveData<List<Komyuniti>?>()
    private val selectedKomyuniti = MutableLiveData<Komyuniti?>()

    fun getKomyunities(): LiveData<List<Komyuniti>?> {
        return komyunities
    }

    fun setSelectedKomyuniti(obj: Komyuniti?) {
        selectedKomyuniti.postValue(obj)
    }

    fun getSelectedKomyuniti(): LiveData<Komyuniti?> {
        return selectedKomyuniti
    }

    fun fetchKomyunitis(apollo: ApolloClient, userId: String) {
        viewModelScope.launch {
            val res = apollo.query(GetKomunitiesOverviewQuery(userId = Input.fromNullable(userId))).await()

            if(res.data == null || res.data?.komyunities == null) {
                komyunities.postValue(null)
            }

            // wrap into data classes
            // wrap into data class
            val _komyunitis = mutableListOf<Komyuniti>()
            for (obj in res.data?.komyunities!!) {
                // wrap members into data classes
                val members = mutableListOf<User>()
                for (m in obj!!.members!!) {
                    val member = User(m._id, name = m.name)
                    members.add(member)
                }

                val komyuniti = Komyuniti(obj._id, name = obj.name, members = members)
                _komyunitis.add(komyuniti)
            }
            komyunities.postValue(_komyunitis)
        }
    }
}