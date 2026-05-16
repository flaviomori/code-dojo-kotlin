package com.alice.payouts.paymentsplit

import com.alice.payouts.BadRequestException
import com.alice.payouts.NotFoundException
import com.alice.payouts.auth.AuthOperator
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PaymentSplitService(private val repository: PaymentSplitRepository) {
    fun get(id: UUID): PaymentSplit =
        repository.findById(id) ?: throw NotFoundException("payment split $id not found")

    fun listByPractitioner(practitionerId: UUID): List<PaymentSplit> =
        repository.findByPractitionerId(practitionerId)

    fun create(request: PaymentSplitCreateRequest, operator: AuthOperator): PaymentSplit {
        val practitionerId = parseUuid(request.practitionerId, "practitionerId")
        val bankAccountId = parseUuid(request.bankAccountId, "bankAccountId")
        val share = parseDecimal(request.sharePercentage, "sharePercentage")
        val now = Instant.now()
        val split = PaymentSplit(
            id = UUID.randomUUID(),
            practitionerId = practitionerId,
            bankAccountId = bankAccountId,
            sharePercentage = share,
            procedureRef = request.procedureRef,
            notes = request.notes,
            registeredBy = operator.id,
            updatedBy = operator.id,
            updatedAt = now,
            createdAt = now,
        )
        return repository.save(split)
    }

    fun update(id: UUID, patch: PaymentSplitUpdateRequest, operator: AuthOperator): PaymentSplit {
        val current = repository.findById(id)
            ?: throw NotFoundException("payment split $id not found")

        val updated = current.copy(
            practitionerId = patch.practitionerId?.let { parseUuid(it, "practitionerId") } ?: current.practitionerId,
            bankAccountId = patch.bankAccountId?.let { parseUuid(it, "bankAccountId") } ?: current.bankAccountId,
            sharePercentage = patch.sharePercentage?.let { parseDecimal(it, "sharePercentage") } ?: current.sharePercentage,
            procedureRef = patch.procedureRef ?: current.procedureRef,
            notes = patch.notes ?: current.notes,
            updatedBy = operator.id,
            updatedAt = Instant.now(),
        )

        return repository.save(updated)
    }

    private fun parseUuid(value: String, field: String): UUID = try {
        UUID.fromString(value)
    } catch (_: IllegalArgumentException) {
        throw BadRequestException("invalid $field")
    }

    private fun parseDecimal(value: String, field: String): BigDecimal = try {
        BigDecimal(value)
    } catch (_: NumberFormatException) {
        throw BadRequestException("invalid $field")
    }
}
