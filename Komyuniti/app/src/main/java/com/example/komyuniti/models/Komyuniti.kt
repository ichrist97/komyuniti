package com.example.komyuniti.models

import java.time.LocalDate

data class Komyuniti(
    val id: String,
    val name: String? = null,
    val members: List<User>? = null,
    val admin: User? = null,
    val createdAt: LocalDate? = null
) {
}