package com.example.komyuniti.ui.profile

import LoggedInUserQuery
import CurrentUserNameQuery
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Base64
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.coroutines.await
import com.example.komyuniti.models.QRPayload
import com.example.komyuniti.models.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.google.gson.Gson
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import java.security.KeyPair


class ProfileViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        //value = "This is notifications Fragment"
    }

    suspend fun generateQRBitmap(apollo: ApolloClient, keyPair: KeyPair): Bitmap? {
        var curUser: User?
        withContext(Dispatchers.IO) {
            curUser = getLoggedInUser(apollo)
        }

        if(curUser == null) {
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

    val text: LiveData<String> = _text

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

}