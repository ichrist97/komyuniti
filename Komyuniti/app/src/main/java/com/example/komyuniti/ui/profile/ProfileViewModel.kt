package com.example.komyuniti.ui.profile

import LoggedInUserQuery
import CurrentUserNameQuery
import GetFriendsQuery
import GetKomunitiesOverviewQuery
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Input
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.Komyuniti
import com.example.komyuniti.models.QRPayload
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.launch
import type.GetUserInput
import java.security.KeyPair


class ProfileViewModel : ViewModel() {

    private val friends = MutableLiveData<List<User>?>()
    private val komyunitis = MutableLiveData<List<Komyuniti>?>()

    fun getFriends(): LiveData<List<User>?> {
        return friends
    }

    fun getKomyunitis(): LiveData<List<Komyuniti>?> {
        return komyunitis
    }

    suspend fun generateQRBitmap(apollo: ApolloClient, keyPair: KeyPair): Bitmap? {
        var curUser: User?
        withContext(Dispatchers.IO) {
            curUser = getLoggedInUser(apollo)
        }

        if (curUser == null) {
            return null
        }

        val pubKeyBytes = Base64.encode(keyPair.public.encoded, Base64.DEFAULT)
        val pubKeyStr = String(pubKeyBytes)

        // wrap into payload and serialize to json
        val payload = QRPayload(curUser?.id!!, pubKeyStr)
        val gson = Gson()
        val json = gson.toJson(payload)
        // now we have double base64 encoding, maybe there is a better apporach?
        val jsonEncoded = Base64.encode(json.toByteArray(), Base64.DEFAULT).toString(Charsets.UTF_8)

        // generate qr code
        val writer = QRCodeWriter()
        val bitMatrix = writer.encode(jsonEncoded, BarcodeFormat.QR_CODE, 512, 512)
        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }
        return bitmap
    }

    private suspend fun getLoggedInUser(
        apollo: ApolloClient
    ): User? {
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

    suspend fun getCurrentUserName(
        apollo: ApolloClient
    ): User? {
        //res is
        var res: Response<CurrentUserNameQuery.Data>
        withContext(Dispatchers.IO) {
            res = apollo.query(CurrentUserNameQuery()).await()
        }
        if (res.data == null || res.data?.currentUser == null) {
            return null
        }
        return User(res.data?.currentUser?._id!!, name = res.data?.currentUser?.name)
    }

    fun getFriends(apollo: ApolloClient, userId: String) {
        viewModelScope.launch {
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

    fun getKomyunitis(apollo: ApolloClient, userId: String) {
        viewModelScope.launch {
            val res = apollo.query(GetKomunitiesOverviewQuery(userId = Input.fromNullable(userId)))
                .await()

            if (res.data == null || res.data?.komyunities == null) {
                komyunitis.postValue(null)
            }

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
            komyunitis.postValue(_komyunitis)
        }
    }
}