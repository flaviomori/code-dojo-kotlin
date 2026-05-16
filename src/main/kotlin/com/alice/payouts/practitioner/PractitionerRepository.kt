package com.alice.payouts.practitioner

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class PractitionerRepository {
    fun findById(id: UUID): Practitioner? = transaction {
        PractitionerTable.selectAll().where { PractitionerTable.id eq id }
            .singleOrNull()?.toPractitioner()
    }

    fun findAll(): List<Practitioner> = transaction {
        PractitionerTable.selectAll()
            .orderBy(PractitionerTable.fullName)
            .map { it.toPractitioner() }
    }

    private fun ResultRow.toPractitioner() = Practitioner(
        id = this[PractitionerTable.id],
        fullName = this[PractitionerTable.fullName],
        crm = this[PractitionerTable.crm],
        specialty = this[PractitionerTable.specialty],
        createdAt = this[PractitionerTable.createdAt],
    )
}
