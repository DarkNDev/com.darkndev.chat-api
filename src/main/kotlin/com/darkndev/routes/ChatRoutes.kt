package com.darkndev.routes

import com.darkndev.data.ChatController
import com.darkndev.models.ChatSession
import com.darkndev.utils.DuplicateMemberException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sessions.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.channels.consumeEach

fun Route.chatSocket(chatController: ChatController) {
    webSocket("/chat") {
        val session = call.sessions.get<ChatSession>()
        if (session == null) {
            close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
            return@webSocket
        }
        try {
            chatController.onJoin(
                username = session.username,
                sessionId = session.sessionId,
                socket = this
            )
            incoming.consumeEach { frame ->
                if (frame is Frame.Text) {
                    chatController.sendMessage(
                        senderUsername = session.username,
                        text = frame.readText()
                    )
                }
            }
        } catch (e: DuplicateMemberException) {
            call.respond(HttpStatusCode.Conflict)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            chatController.tryDisconnect(session.username)
        }
    }
}

fun Route.getAllMessages(chatController: ChatController) {
    get("/messages") {
        call.respond(
            HttpStatusCode.OK,
            chatController.getAllMessages()
        )
    }
}