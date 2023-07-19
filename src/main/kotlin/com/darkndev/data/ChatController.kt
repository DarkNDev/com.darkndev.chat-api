package com.darkndev.data

import com.darkndev.models.Member
import com.darkndev.models.Message
import com.darkndev.utils.DuplicateMemberException
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val messageDao: MessageDao
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(username: String, sessionId: String, socket: WebSocketSession) {
        if (members.containsKey(username)) {
            throw DuplicateMemberException()
        }
        members[username] = Member(username, sessionId, socket)
    }

    suspend fun sendMessage(senderUsername: String, text: String) {
        val message = messageDao.insertMessage(text, senderUsername, System.currentTimeMillis())
        members.values.forEach { member ->
            val parsedMessage = Json.encodeToString(message)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages() = messageDao.getAllMessages()

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()
        if (members.containsKey(username)) {
            members.remove(username)
        }
    }
}