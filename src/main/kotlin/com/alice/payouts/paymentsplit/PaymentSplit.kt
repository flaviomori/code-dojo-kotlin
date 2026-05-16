package com.alice.payouts.paymentsplit

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

data class PaymentSplit(
    val id: UUID,
    val practitionerId: UUID,
    val bankAccountId: UUID,
    val sharePercentage: BigDecimal,
    val procedureRef: String?,
    val notes: String?,
    val registeredBy: UUID,
    val updatedBy: UUID,
    val updatedAt: Instant,
    val createdAt: Instant,
)
