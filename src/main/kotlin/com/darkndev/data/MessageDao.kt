package com.darkndev.data

import com.darkndev.data.DatabaseFactory.dbQuery
import com.darkndev.models.Message
import com.darkndev.models.Messages
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

class MessageDao {

    private fun resultRowToMessage(row: ResultRow) = Message(
        id = row[Messages.id],
        text = row[Messages.text],
        username = row[Messages.username],
        timestamp = row[Messages.timestamp]
    )

    suspend fun getAllMessages() = dbQuery {
        Messages.selectAll()
            .sortedByDescending { Messages.timestamp }
            .map(::resultRowToMessage)
    }

    suspend fun insertMessage(text: String, username: String, timestamp: Long) = dbQuery {
        val insertStatement = Messages.insert {
            it[Messages.text] = text
            it[Messages.username] = username
            it[Messages.timestamp] = timestamp
        }
        insertStatement.resultedValues?.singleOrNull()?.let(::resultRowToMessage)
    }
}