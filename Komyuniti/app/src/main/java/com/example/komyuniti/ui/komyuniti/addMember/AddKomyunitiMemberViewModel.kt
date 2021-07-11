package com.example.komyuniti.ui.komyuniti.addMember

import AddKomyunitiMembersMutation
import GetFriendsQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import type.AddKomyunitiMemberInput
import type.GetUserInput

class AddKomyunitiMemberViewModel : ViewModel() {

    private val friends = MutableLiveData<List<User>?>(null)
    private val newMember = MutableLiveData<List<User>>(listOf())
    private val finishedAdding = MutableLiveData(false)
    private val komyuniti = MutableLiveData<Komyuniti>()

    fun getFinishedAdding(): LiveData<Boolean> {
        return finishedAdding
    }

    fun getKomyuniti(): LiveData<Komyuniti> {
        return komyuniti
    }

    fun setKomyuniti(obj: Komyuniti) {
        komyuniti.postValue(obj)
    }

    fun getFriends(): LiveData<List<User>?> {
        return friends
    }

    fun setNewMember(data: List<User>) {
        newMember.postValue(data)
    }

    fun getNewMember(): LiveData<List<User>> {
        return newMember
    }

    suspend fun fetchFriends(apollo: ApolloClient, userId: String) {
        withContext(Dispatchers.IO) {
            val input = GetUserInput(id = Input.fromNullable(userId))
            val res = apollo.query(GetFriendsQuery(input = input)).await()

            if (res.data == null || res.data?.user == null) {
                friends.postValue(null)
            }

            // wrap into data class
            val _friends = mutableListOf<User>()
            for (user in res.data?.user?.friends!!) {
                val friend = User(user._id, name = user.name)

                // filter for friends which are not already member of the komyuniti
                if (!komyuniti.value?.members?.contains(friend)!!) {
                    _friends.add(friend)
                }
            }
            friends.postValue(_friends)
        }
    }

    fun addMembers(apollo: ApolloClient, komyunitiId: String, members: List<User>) {
        viewModelScope.launch {
            val userIds = members.map {
                it.id
            }
            val input = AddKomyunitiMemberInput(id = komyunitiId, userIds = userIds)
            val res = apollo.mutate(AddKomyunitiMembersMutation(input = input)).await()

            if (res.data == null || res.data?.addKomyunitiMember == null) {
                finishedAdding.postValue(null)
            }

            finishedAdding.postValue(true)
        }
    }
}