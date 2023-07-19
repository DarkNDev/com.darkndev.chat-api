package com.darkndev.models

import kotlinx.serialization.Serializable
import org.jetbrains.exposed.sql.Table

@Serializable
data class Message(
    val id: Int,
    val text: String,
    val username: String,
    val timestamp: Long
)

object Messages : Table() {
    val id = integer("id").autoIncrement()
    val text = varchar("text", 512)
    val username = varchar("username", 64)
    val timestamp = long("timestamp")

    override val primaryKey = PrimaryKey(id)
}
