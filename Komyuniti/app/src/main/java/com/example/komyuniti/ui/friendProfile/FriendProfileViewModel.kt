package com.example.komyuniti.ui.friendProfile

import GetFriendInfoQuery
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.User
import kotlinx.coroutines.launch
import type.GetUserInput
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FriendProfileViewModel : ViewModel() {

    private val user = MutableLiveData<User?>()
    private val friendId = MutableLiveData<String>()

    fun getFriendId(): LiveData<String> {
        return friendId
    }

    fun setFriendId(friendId: String) {
        this.friendId.postValue(friendId)
    }

    fun getUser(): LiveData<User?> {
        return user
    }

    fun fetchFriend(apollo: ApolloClient, friendId: String) {
        viewModelScope.launch {
            val input = GetUserInput(id = Input.fromNullable(friendId))
            val res = apollo.query(GetFriendInfoQuery(input = input)).await()
            if (res.data == null || res.data?.user == null) {
                user.postValue(null)
            }

            // parse date
            val dateStr = res.data?.user?.createdAt!!.split(" ")[0]
            val date =
                LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
            val _user = User(
                res.data?.user?._id!!,
                name = res.data?.user?.name!!,
                createdAt = date
            )
            user.postValue(_user)
        }
    }
}