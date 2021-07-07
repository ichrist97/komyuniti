package com.example.komyuniti.models

data class ChatMessage(
    val sender: User,
    val createdAt: String,
    val text: String,
    val eventId: String? = null,
) {
}