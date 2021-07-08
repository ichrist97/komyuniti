package com.example.komyuniti.models

data class Event(
    val id: String,
    val name: String? = null,
    val date: String? = null,
    val admin: User? = null,
    val members: Array<User>? = null,
    val createdAt: String? = null,
    val komyuniti: Komyuniti? = null,
    val adress: String? = null
) {

}