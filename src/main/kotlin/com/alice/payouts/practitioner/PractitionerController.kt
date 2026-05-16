package com.alice.payouts.practitioner

import com.alice.payouts.BadRequestException
import com.alice.payouts.NotFoundException
import com.alice.payouts.auth.requireAuth
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class PractitionerResponse(
    val id: String,
    val fullName: String,
    val crm: String,
    val specialty: String,
)

private fun Practitioner.toResponse() = PractitionerResponse(
    id = id.toString(),
    fullName = fullName,
    crm = crm,
    specialty = specialty,
)

fun Route.practitionerRoutes() {
    val repository = PractitionerRepository()

    route("/practitioners") {
        get {
            call.requireAuth()
            call.respond(repository.findAll().map { it.toResponse() })
        }
        get("{id}") {
            call.requireAuth()
            val id = call.parameters["id"]?.let { UUID.fromString(it) }
                ?: throw BadRequestException("invalid id")
            val practitioner = repository.findById(id)
                ?: throw NotFoundException("practitioner $id not found")
            call.respond(practitioner.toResponse())
        }
    }
}
