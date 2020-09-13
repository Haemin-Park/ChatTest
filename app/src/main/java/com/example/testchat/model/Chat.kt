package com.example.testchat.model

import com.beust.klaxon.Json

data class Chat (
    @Json(name = "sender")
    val sender: String,
    @Json(name = "message")
    val message: String
)