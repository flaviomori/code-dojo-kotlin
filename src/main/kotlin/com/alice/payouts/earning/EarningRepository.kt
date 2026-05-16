package com.alice.payouts.earning

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import java.util.UUID

class EarningRepository {
    fun findById(id: UUID): Earning? = transaction {
        EarningTable.selectAll().where { EarningTable.id eq id }
            .singleOrNull()?.toEarning()
    }

    fun findByPractitionerId(practitionerId: UUID): List<Earning> = transaction {
        EarningTable.selectAll()
            .where { EarningTable.practitionerId eq practitionerId }
            .orderBy(EarningTable.competencyMonth to SortOrder.DESC)
            .map { it.toEarning() }
    }

    fun save(earning: Earning): Earning = transaction {
        val existing = EarningTable.selectAll()
            .where { EarningTable.id eq earning.id }.count() > 0
        if (existing) {
            EarningTable.update({ EarningTable.id eq earning.id }) {
                it[practitionerId] = earning.practitionerId
                it[amount] = earning.amount
                it[competencyMonth] = earning.competencyMonth
                it[notes] = earning.notes
                it[updatedBy] = earning.updatedBy
                it[updatedAt] = earning.updatedAt
            }
        } else {
            EarningTable.insert {
                it[id] = earning.id
                it[practitionerId] = earning.practitionerId
                it[amount] = earning.amount
                it[competencyMonth] = earning.competencyMonth
                it[notes] = earning.notes
                it[updatedBy] = earning.updatedBy
                it[updatedAt] = earning.updatedAt
                it[createdAt] = earning.createdAt
            }
        }
        earning
    }

    private fun ResultRow.toEarning() = Earning(
        id = this[EarningTable.id],
        practitionerId = this[EarningTable.practitionerId],
        amount = this[EarningTable.amount],
        competencyMonth = this[EarningTable.competencyMonth],
        notes = this[EarningTable.notes],
        updatedBy = this[EarningTable.updatedBy],
        updatedAt = this[EarningTable.updatedAt],
        createdAt = this[EarningTable.createdAt],
    )
}
