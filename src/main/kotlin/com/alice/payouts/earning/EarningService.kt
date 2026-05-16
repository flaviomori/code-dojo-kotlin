package com.alice.payouts.earning

import com.alice.payouts.BadRequestException
import com.alice.payouts.NotFoundException
import com.alice.payouts.auth.AuthOperator
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class EarningService(private val repository: EarningRepository) {
    fun get(id: UUID): Earning =
        repository.findById(id) ?: throw NotFoundException("earning $id not found")

    fun listByPractitioner(practitionerId: UUID): List<Earning> =
        repository.findByPractitionerId(practitionerId)

    fun create(request: EarningCreateRequest, operator: AuthOperator): Earning {
        val practitionerId = parseUuid(request.practitionerId, "practitionerId")
        val amount = parseAmount(request.amount)
        val now = Instant.now()
        val earning = Earning(
            id = UUID.randomUUID(),
            practitionerId = practitionerId,
            amount = amount,
            competencyMonth = request.competencyMonth,
            notes = request.notes,
            updatedBy = operator.id,
            updatedAt = now,
            createdAt = now,
        )
        return repository.save(earning)
    }

    fun update(id: UUID, patch: EarningUpdateRequest, operator: AuthOperator): Earning {
        val current = repository.findById(id)
            ?: throw NotFoundException("earning $id not found")

        val updated = current.copy(
            practitionerId = patch.practitionerId?.let { parseUuid(it, "practitionerId") } ?: current.practitionerId,
            amount = patch.amount?.let { parseAmount(it) } ?: current.amount,
            competencyMonth = patch.competencyMonth ?: current.competencyMonth,
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

    private fun parseAmount(value: String): BigDecimal = try {
        BigDecimal(value)
    } catch (_: NumberFormatException) {
        throw BadRequestException("invalid amount")
    }
}
