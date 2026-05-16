package com.alice.payouts.paymentsplit

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class PaymentSplitRepository {
    fun findById(id: UUID): PaymentSplit? = transaction {
        PaymentSplitTable.selectAll().where { PaymentSplitTable.id eq id }
            .singleOrNull()?.toPaymentSplit()
    }

    fun findByPractitionerId(practitionerId: UUID): List<PaymentSplit> = transaction {
        PaymentSplitTable.selectAll()
            .where { PaymentSplitTable.practitionerId eq practitionerId }
            .orderBy(PaymentSplitTable.createdAt to SortOrder.DESC)
            .map { it.toPaymentSplit() }
    }

    fun save(split: PaymentSplit): PaymentSplit = transaction {
        val existing = PaymentSplitTable.selectAll()
            .where { PaymentSplitTable.id eq split.id }.count() > 0
        if (existing) {
            PaymentSplitTable.update({ PaymentSplitTable.id eq split.id }) {
                it[practitionerId] = split.practitionerId
                it[bankAccountId] = split.bankAccountId
                it[sharePercentage] = split.sharePercentage
                it[procedureRef] = split.procedureRef
                it[notes] = split.notes
                it[updatedBy] = split.updatedBy
                it[updatedAt] = split.updatedAt
            }
        } else {
            PaymentSplitTable.insert {
                it[id] = split.id
                it[practitionerId] = split.practitionerId
                it[bankAccountId] = split.bankAccountId
                it[sharePercentage] = split.sharePercentage
                it[procedureRef] = split.procedureRef
                it[notes] = split.notes
                it[registeredBy] = split.registeredBy
                it[updatedBy] = split.updatedBy
                it[updatedAt] = split.updatedAt
                it[createdAt] = split.createdAt
            }
        }
        split
    }

    private fun ResultRow.toPaymentSplit() = PaymentSplit(
        id = this[PaymentSplitTable.id],
        practitionerId = this[PaymentSplitTable.practitionerId],
        bankAccountId = this[PaymentSplitTable.bankAccountId],
        sharePercentage = this[PaymentSplitTable.sharePercentage],
        procedureRef = this[PaymentSplitTable.procedureRef],
        notes = this[PaymentSplitTable.notes],
        registeredBy = this[PaymentSplitTable.registeredBy],
        updatedBy = this[PaymentSplitTable.updatedBy],
        updatedAt = this[PaymentSplitTable.updatedAt],
        createdAt = this[PaymentSplitTable.createdAt],
    )
}
