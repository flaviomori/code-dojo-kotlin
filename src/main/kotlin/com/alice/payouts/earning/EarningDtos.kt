package com.alice.payouts.earning

import kotlinx.serialization.Serializable

@Serializable
data class EarningResponse(
    val id: String,
    val practitionerId: String,
    val amount: String,
    val competencyMonth: String,
    val notes: String?,
    val updatedBy: String,
    val updatedAt: String,
)

@Serializable
data class EarningUpdateRequest(
    val practitionerId: String? = null,
    val amount: String? = null,
    val competencyMonth: String? = null,
    val notes: String? = null,
)

@Serializable
data class EarningCreateRequest(
    val practitionerId: String,
    val amount: String,
    val competencyMonth: String,
    val notes: String? = null,
)

fun Earning.toResponse() = EarningResponse(
    id = id.toString(),
    practitionerId = practitionerId.toString(),
    amount = amount.toPlainString(),
    competencyMonth = competencyMonth,
    notes = notes,
    updatedBy = updatedBy.toString(),
    updatedAt = updatedAt.toString(),
)
