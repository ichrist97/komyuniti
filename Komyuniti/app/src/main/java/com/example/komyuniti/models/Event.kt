package com.example.komyuniti.models

import java.time.LocalDate

data class Event(
    val id: String,
    val name: String? = null,
    val date: LocalDate? = null,
    val admin: User? = null,
    val members: List<User>? = null,
    val createdAt: LocalDate? = null,
    val komyuniti: Komyuniti? = null,
    val address: String? = null
) {

}