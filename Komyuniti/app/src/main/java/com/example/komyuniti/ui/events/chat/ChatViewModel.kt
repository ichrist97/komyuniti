package com.example.komyuniti.ui.events.chat

import GetChatMessagesQuery
import ListenChatSubscription
import LoggedInUserQuery
import SendChatMsgMutation
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class ChatViewModel : ViewModel() {

    // init as empty list
    val data = MutableLiveData(listOf<ChatMessage>())

    private val event = MutableLiveData<Event>()

    fun getEvent(): LiveData<Event> {
        return event
    }

    fun setEvent(e: Event) {
        event.postValue(e)
    }

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

        // parse date
        val dateParts = res.data?.createChatMsg?.createdAt?.split(" ")
        val dateStr = dateParts!![0]
        val timeStr = dateParts[1]
        val date =
            LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)

        return ChatMessage(
            user,
            date,
            res.data?.createChatMsg?.text!!,
            timeStr
        )
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

            // parse date
            val dateParts = msg.createdAt?.split(" ")
            val dateStr = dateParts!![0]
            val timeStr = dateParts[1]
            val date =
                LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)

            val msgObj = ChatMessage(user, date, msg.text, timeStr)
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

                // parse date
                val dateParts = res.data?.msgCreated?.createdAt?.split(" ")
                val dateStr = dateParts!![0]
                val timeStr = dateParts[1]
                val date =
                    LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)

                val msg = ChatMessage(
                    user,
                    date,
                    res.data?.msgCreated?.text!!,
                    timeStr
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
