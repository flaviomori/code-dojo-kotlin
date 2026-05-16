package com.alice.payouts.earning

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class Earning(
    val id: UUID,
    val practitionerId: UUID,
    val amount: BigDecimal,
    val competencyMonth: String,
    val notes: String?,
    val updatedBy: UUID,
    val updatedAt: Instant,
    val createdAt: Instant,
)
