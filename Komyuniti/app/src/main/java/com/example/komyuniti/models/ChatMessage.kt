package com.example.komyuniti.models

import java.time.LocalDate

data class ChatMessage(
    val sender: User,
    val createdAt: LocalDate,
    val text: String,
    val time: String,
    val eventId: String? = null,
) {
}