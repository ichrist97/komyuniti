package com.example.komyuniti.models

import java.time.LocalDate

data class User(
    val id: String,
    val email: String? = null,
    val name: String? = null,
    val createdAt: LocalDate? = null,
    val role: String? = null,
    val friends: List<User>? = null
) {

}