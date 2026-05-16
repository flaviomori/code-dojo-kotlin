package com.alice.payouts.bankaccount

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object BankAccountTable : Table("bank_accounts") {
    val id = uuid("id")
    val practitionerId = uuid("practitioner_id")
    val bankCode = text("bank_code")
    val agency = text("agency")
    val accountNumber = text("account_number")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
