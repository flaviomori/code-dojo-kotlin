package com.alice.payouts.practitioner

import java.time.Instant
import java.util.UUID

data class Practitioner(
    val id: UUID,
    val fullName: String,
    val crm: String,
    val specialty: String,
    val createdAt: Instant,
)
