package com.alice.payouts.paymentsplit

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object PaymentSplitTable : Table("payment_splits") {
    val id = uuid("id")
    val practitionerId = uuid("practitioner_id")
    val bankAccountId = uuid("bank_account_id")
    val sharePercentage = decimal("share_percentage", 5, 2)
    val procedureRef = text("procedure_ref").nullable()
    val notes = text("notes").nullable()
    val registeredBy = uuid("registered_by")
    val updatedBy = uuid("updated_by")
    val updatedAt = timestamp("updated_at")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
