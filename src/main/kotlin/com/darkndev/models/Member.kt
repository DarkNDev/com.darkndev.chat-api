package com.darkndev.models

import io.ktor.server.websocket.*

data class Member(
    val username: String,
    val sessionId: String,
    val socket: WebSocketServerSession
)
