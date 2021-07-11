package com.example.komyuniti.ui.newEvent.addMembers

import GetFriendsQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.GetUserInput

class NewEventAddMembersViewModel : ViewModel() {

    private val friends = MutableLiveData<List<User>?>(null)
    private val chosenMembers = MutableLiveData<List<User>>(listOf())

    fun getFriends(): LiveData<List<User>?> {
        return friends
    }

    fun setChosenMembers(data: List<User>) {
        chosenMembers.postValue(data)
    }

    fun getChosenMembers(): LiveData<List<User>> {
        return chosenMembers
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
                _friends.add(friend)
            }
            friends.postValue(_friends)
        }
    }

}