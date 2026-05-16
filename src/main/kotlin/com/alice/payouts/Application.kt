package com.alice.payouts

import com.alice.payouts.auth.AuthMiddleware
import com.alice.payouts.earning.earningRoutes
import com.alice.payouts.paymentsplit.paymentSplitRoutes
import com.alice.payouts.practitioner.practitionerRoutes
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.routing

fun main() {
    Database.init()
    embeddedServer(Netty, port = 8080) {
        install(ContentNegotiation) { json() }
        install(CallLogging)
        install(AuthMiddleware)
        install(StatusPages) {
            exception<NotFoundException> { call, e -> call.respond(HttpStatusCode.NotFound, mapOf("error" to e.message)) }
            exception<BadRequestException> { call, e -> call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message)) }
        }
        routing {
            practitionerRoutes()
            earningRoutes()
            paymentSplitRoutes()
        }
    }.start(wait = true)
}

class NotFoundException(message: String) : RuntimeException(message)
class BadRequestException(message: String) : RuntimeException(message)
