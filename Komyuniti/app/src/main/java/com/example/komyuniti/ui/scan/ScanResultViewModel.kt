package com.example.komyuniti.ui.scan

import AddFriendMutation
import GetUserQuery
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.QRPayload
import com.example.komyuniti.models.User
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import type.AddFriendInput
import type.GetUserInput

class ScanResultViewModel : ViewModel() {

    private val qrBarcode = MutableLiveData<String>()

    fun setQR(payload: String) {
        qrBarcode.value = payload
    }

    fun getQR(): MutableLiveData<String> {
        return qrBarcode
    }

    private fun decodeQRPayload(payload: String): QRPayload {
        val gson = Gson()
        val json = Base64.decode(payload, Base64.DEFAULT).toString(Charsets.UTF_8)
        return gson.fromJson(json, QRPayload::class.java)
    }

    suspend fun fetchUser(apollo: ApolloClient): User? {
        val qrPayload = decodeQRPayload(qrBarcode.value!!)

        var res: Response<GetUserQuery.Data>
        withContext(Dispatchers.IO) {
            val input = Input.fromNullable(GetUserInput(Input.fromNullable(qrPayload.id)))
            res = apollo.query(GetUserQuery(input = input)).await()
        }

        if (res.data == null || res.data?.user == null) {
            return null
        }
        return User(res.data?.user?._id!!, name = res.data?.user?.name!!)
    }


    suspend fun addFriend(apollo: ApolloClient): User? {
        val qrPayload = decodeQRPayload(qrBarcode.value!!)

        var res: Response<AddFriendMutation.Data>
        withContext(Dispatchers.Default) {
            res = addFriendRequest(apollo, qrPayload.id)
        }

        if (res.data == null || res.data?.addFriend == null) {
            return null
        }

        return User(res.data?.addFriend?._id!!)
    }

    private suspend fun addFriendRequest(
        apollo: ApolloClient,
        friendId: String
    ): Response<AddFriendMutation.Data> {
        val input = AddFriendInput(Input.fromNullable(friendId))
        return apollo.mutate(
            AddFriendMutation(
                input = input
            )
        ).await()
    }

}