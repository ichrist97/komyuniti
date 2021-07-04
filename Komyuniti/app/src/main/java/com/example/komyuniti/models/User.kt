package com.example.komyuniti.models

data class User(
    val id: String,
    val email: String? = null,
    val name: String? = null,
    val createdAt: String? = null,
    val role: String? = null,
    val friends: Array<User>? = null
) {

}