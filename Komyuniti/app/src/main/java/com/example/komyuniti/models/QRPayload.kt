package com.example.komyuniti.models

import com.google.gson.annotations.SerializedName

data class QRPayload(
    @SerializedName("id") val id: String,
    @SerializedName("publicKey") val publicKey: String
) {
}