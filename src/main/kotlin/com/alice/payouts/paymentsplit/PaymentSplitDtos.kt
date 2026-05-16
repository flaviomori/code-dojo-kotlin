package com.alice.payouts.paymentsplit

import kotlinx.serialization.Serializable

@Serializable
data class PaymentSplitResponse(
    val id: String,
    val practitionerId: String,
    val bankAccountId: String,
    val sharePercentage: String,
    val procedureRef: String?,
    val notes: String?,
    val registeredBy: String,
    val updatedBy: String,
    val updatedAt: String,
)

@Serializable
data class PaymentSplitUpdateRequest(
    val practitionerId: String? = null,
    val bankAccountId: String? = null,
    val sharePercentage: String? = null,
    val procedureRef: String? = null,
    val notes: String? = null,
)

@Serializable
data class PaymentSplitCreateRequest(
    val practitionerId: String,
    val bankAccountId: String,
    val sharePercentage: String,
    val procedureRef: String? = null,
    val notes: String? = null,
)

fun PaymentSplit.toResponse() = PaymentSplitResponse(
    id = id.toString(),
    practitionerId = practitionerId.toString(),
    bankAccountId = bankAccountId.toString(),
    sharePercentage = sharePercentage.toPlainString(),
    procedureRef = procedureRef,
    notes = notes,
    registeredBy = registeredBy.toString(),
    updatedBy = updatedBy.toString(),
    updatedAt = updatedAt.toString(),
)
