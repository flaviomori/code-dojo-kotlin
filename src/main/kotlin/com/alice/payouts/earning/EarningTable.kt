package com.alice.payouts.earning

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object EarningTable : Table("earnings") {
    val id = uuid("id")
    val practitionerId = uuid("practitioner_id")
    val amount = decimal("amount", 12, 2)
    val competencyMonth = text("competency_month")
    val notes = text("notes").nullable()
    val updatedBy = uuid("updated_by")
    val updatedAt = timestamp("updated_at")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
