package com.example.komyuniti.ui.komyuniti.createKomyuniti

import CreateKomyunitiMutation
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.User
import kotlinx.coroutines.launch
import type.CreateKomyunitiInput

class CreateKomyunitiViewModel : ViewModel() {

    private val members = MutableLiveData<List<User>>()
    private val createdKomyuniti = MutableLiveData<Boolean>(false)

    fun getCreatedKomyuniti(): LiveData<Boolean> {
        return createdKomyuniti
    }

    fun setCreatedKomyuniti(state: Boolean) {
        createdKomyuniti.postValue(state)
    }

    fun setMembers(obj: List<User>) {
        members.postValue(obj)
    }

    fun getMembers(): LiveData<List<User>> {
        return members
    }

    fun createKomyuniti(apollo: ApolloClient, name: String, members: List<User>) {
        viewModelScope.launch {
            val userIds = members.map {
                it.id
            }
            val input = CreateKomyunitiInput(name = name, userIds = Input.fromNullable(userIds))
            val res = apollo.mutate(CreateKomyunitiMutation(input = input)).await()

            if (res.data == null || res.data?.createKomyuniti == null) {
                createdKomyuniti.postValue(false)
            }

            createdKomyuniti.postValue(true)
        }
    }
}