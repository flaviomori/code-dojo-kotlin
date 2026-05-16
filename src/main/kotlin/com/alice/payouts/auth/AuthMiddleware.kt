package com.alice.payouts.auth

import com.alice.payouts.BadRequestException
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.util.AttributeKey
import java.util.UUID

val AuthOperatorKey = AttributeKey<AuthOperator>("AuthOperator")

val AuthMiddleware = createApplicationPlugin("AuthMiddleware") {
    onCall { call ->
        val operatorId = call.request.headers["X-Operator-Id"]
            ?: throw BadRequestException("missing X-Operator-Id header")
        val parsed = try {
            UUID.fromString(operatorId)
        } catch (_: IllegalArgumentException) {
            throw BadRequestException("invalid X-Operator-Id")
        }
        call.attributes.put(AuthOperatorKey, AuthOperator(parsed))
    }
}

fun ApplicationCall.requireAuth(): AuthOperator = attributes[AuthOperatorKey]
