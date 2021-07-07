package com.example.komyuniti.ui.events.chat

import GetChatMessagesQuery
import ListenChatSubscription
import LoggedInUserQuery
import SendChatMsgMutation
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.apollographql.apollo.coroutines.toFlow
import com.apollographql.apollo.exception.ApolloException
import com.example.komyuniti.models.ChatMessage
import com.example.komyuniti.models.Event
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.withContext
import type.CreateMsgInput

class ChatViewModel : ViewModel() {

    // init as empty list
    val data = MutableLiveData(listOf<ChatMessage>())

    val event = MutableLiveData<Event>()

    suspend fun getCurrentUser(apollo: ApolloClient): User? {
        var res: Response<LoggedInUserQuery.Data>
        withContext(Dispatchers.IO) {
            res = apollo.query(
                LoggedInUserQuery()
            ).await()
        }

        if (res.data == null || res.data?.currentUser == null) {
            return null
        }

        return User(res.data?.currentUser?._id!!)
    }

    suspend fun sendChatMsg(apollo: ApolloClient, eventId: String, text: String): ChatMessage? {
        val res: Response<SendChatMsgMutation.Data>
        withContext(Dispatchers.IO) {
            val input = CreateMsgInput(eventId = eventId, text = text)
            res = apollo.mutate(SendChatMsgMutation(input = input)).await()
        }

        if (res.data == null || res.data?.createChatMsg == null) {
            return null
        }

        val user =
            User(res.data?.createChatMsg?.user?._id!!, name = res.data?.createChatMsg?.user?.name)
        return ChatMessage(user, res.data?.createChatMsg?.createdAt!!, res.data?.createChatMsg?.text!!)
    }

    suspend fun getChatMsgs(apollo: ApolloClient, eventId: String): List<ChatMessage>? {
        val res: Response<GetChatMessagesQuery.Data>
        withContext(Dispatchers.IO) {
            res = apollo.query(GetChatMessagesQuery(eventId = eventId)).await()
        }

        if (res.data == null || res.data?.chatMsgs == null) {
            return null
        }

        // wrap data into list of data classes
        val msgs = mutableListOf<ChatMessage>()
        for (msg in res.data?.chatMsgs!!) {
            val user = User(msg?.user?._id!!, name = msg.user.name)
            val msgObj = ChatMessage(user, msg.createdAt!!, msg.text)
            msgs.add(msgObj)
        }

        return msgs
    }

    fun subscribeChat(apollo: ApolloClient, eventId: String) {
        apollo.subscribe(ListenChatSubscription(eventId = eventId)).execute(object :
            ApolloSubscriptionCall.Callback<ListenChatSubscription.Data> {
            override fun onResponse(res: Response<ListenChatSubscription.Data>) {
                if (res.data == null || res.data?.msgCreated == null) {
                    Log.d("ChatViewModel", "Received invalid chat message")
                    return
                }

                Log.d("ChatViewModel", "Received chat message")

                // wrap into chat messages classes and set mutablelivedata
                val user = User(
                    res.data?.msgCreated?.user?._id!!,
                    name = res.data?.msgCreated?.user?.name!!
                )
                val msg = ChatMessage(
                    user,
                    res.data?.msgCreated?.createdAt!!,
                    res.data?.msgCreated?.text!!
                )
                data.postValue(data.value?.plus(msg))   // post value because of background thread
            }

            override fun onFailure(e: ApolloException) {
                Log.d("ChatViewModel", e.localizedMessage!!)
            }

            override fun onCompleted() {
            }

            override fun onTerminated() {
                Log.d("ChatViewModel", "Stop listening to chat")
            }

            override fun onConnected() {
                Log.d("ChatViewModel", "Start listening to chat")
            }
        })
    }
}
