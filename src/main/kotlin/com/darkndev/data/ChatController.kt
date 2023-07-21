package com.darkndev.data

import com.darkndev.models.Member
import com.darkndev.utils.DuplicateMemberException
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.util.concurrent.ConcurrentHashMap

class ChatController(
    private val messageDao: MessageDao
) {
    private val members = ConcurrentHashMap<String, Member>()

    fun onJoin(username: String, sessionId: String, socket: WebSocketServerSession) {
        if (members.containsKey(username)) {
            throw DuplicateMemberException()
        }
        members[username] = Member(username, sessionId, socket)
    }

    suspend fun sendMessage(senderUsername: String, text: String) {
        val message = messageDao.insertMessage(text, senderUsername, System.currentTimeMillis())
        members.values.forEach { member ->
            member.socket.sendSerialized(message)
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