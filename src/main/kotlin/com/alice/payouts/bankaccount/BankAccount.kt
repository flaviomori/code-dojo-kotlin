package com.alice.payouts.bankaccount

import java.time.Instant
import java.util.UUID

data class BankAccount(
    val id: UUID,
    val practitionerId: UUID,
    val bankCode: String,
    val agency: String,
    val accountNumber: String,
    val createdAt: Instant,
)
