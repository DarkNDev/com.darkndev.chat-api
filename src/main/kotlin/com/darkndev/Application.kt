package com.darkndev

import com.darkndev.data.ChatController
import com.darkndev.data.DatabaseFactory
import com.darkndev.data.MessageDao
import io.ktor.server.application.*
import com.darkndev.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    DatabaseFactory.init(environment.config)
    configureRouting(ChatController(MessageDao()))
}
