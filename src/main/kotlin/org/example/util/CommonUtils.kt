package org.example.util

import org.springframework.web.reactive.function.server.ServerRequest
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

val ServerRequest.token: String?
    get() = this.headers().firstHeader("Authorization").run {
        if (this != null && this.length > 7) substring(7) else null
    }

fun LocalDateTime.toDate(): Date {
    return Date.from(atZone(ZoneId.systemDefault()).toInstant())
}
