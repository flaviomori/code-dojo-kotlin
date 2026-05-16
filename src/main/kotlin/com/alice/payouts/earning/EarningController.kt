package com.alice.payouts.earning

import com.alice.payouts.BadRequestException
import com.alice.payouts.auth.requireAuth
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route
import java.util.UUID

fun Route.earningRoutes() {
    val service = EarningService(EarningRepository())

    route("/earnings") {
        get {
            call.requireAuth()
            val practitionerId = call.request.queryParameters["practitionerId"]
                ?: throw BadRequestException("practitionerId query parameter is required")
            val parsed = try {
                UUID.fromString(practitionerId)
            } catch (_: IllegalArgumentException) {
                throw BadRequestException("invalid practitionerId")
            }
            call.respond(service.listByPractitioner(parsed).map { it.toResponse() })
        }

        post {
            val operator = call.requireAuth()
            val request = call.receive<EarningCreateRequest>()
            val created = service.create(request, operator)
            call.respond(HttpStatusCode.Created, created.toResponse())
        }

        get("{id}") {
            call.requireAuth()
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: throw BadRequestException("invalid id")
            call.respond(service.get(id).toResponse())
        }

        put("{id}") {
            val operator = call.requireAuth()
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: throw BadRequestException("invalid id")
            val patch = call.receive<EarningUpdateRequest>()
            call.respond(service.update(id, patch, operator).toResponse())
        }
    }
}
