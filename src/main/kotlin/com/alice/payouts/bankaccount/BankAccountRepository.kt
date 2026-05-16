package com.alice.payouts.bankaccount

import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

class BankAccountRepository {
    fun findById(id: UUID): BankAccount? = transaction {
        BankAccountTable.selectAll().where { BankAccountTable.id eq id }
            .singleOrNull()?.toBankAccount()
    }

    fun findByPractitioner(practitionerId: UUID): List<BankAccount> = transaction {
        BankAccountTable.selectAll().where { BankAccountTable.practitionerId eq practitionerId }
            .map { it.toBankAccount() }
    }

    private fun ResultRow.toBankAccount() = BankAccount(
        id = this[BankAccountTable.id],
        practitionerId = this[BankAccountTable.practitionerId],
        bankCode = this[BankAccountTable.bankCode],
        agency = this[BankAccountTable.agency],
        accountNumber = this[BankAccountTable.accountNumber],
        createdAt = this[BankAccountTable.createdAt],
    )
}
