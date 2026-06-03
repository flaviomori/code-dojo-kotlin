package com.alice.payouts.earning

import com.alice.payouts.Database
import com.alice.payouts.auth.AuthOperator
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class EarningServiceTest {

    companion object {
        @JvmStatic
        @BeforeAll
        fun setupDatabase() = Database.init()
    }

    private val repository = EarningRepository()
    private val service = EarningService(repository)
    private val practitionerId = UUID.fromString("11111111-1111-1111-1111-111111111111")
    private val operator = AuthOperator(UUID.fromString("99999999-9999-9999-9999-999999999999"))

    @BeforeEach
    fun cleanEarnings() {
        transaction {
            EarningTable.deleteAll()
        }
    }

    private fun seedEarning(
        amount: BigDecimal = BigDecimal("5000.00"),
        competencyMonth: String = "2026-10",
        notes: String? = "Repasse mensal",
    ): UUID {
        val now = Instant.now()
        val earning = Earning(
            id = UUID.randomUUID(),
            practitionerId = practitionerId,
            amount = amount,
            competencyMonth = competencyMonth,
            notes = notes,
            updatedBy = operator.id,
            updatedAt = now,
            createdAt = now,
        )
        repository.save(earning)
        return earning.id
    }

    @Test
    fun `update changes the amount when provided`() {
        val id = seedEarning(amount = BigDecimal("5000.00"))

        val result = service.update(
            id,
            EarningUpdateRequest(amount = "4850.00"),
            operator,
        )

        assertEquals(0, BigDecimal("4850.00").compareTo(result.amount))
        val persisted = repository.findById(id)
        assertNotNull(persisted)
        assertEquals(0, BigDecimal("4850.00").compareTo(persisted!!.amount))
    }

    @Test
    fun `update preserves untouched fields`() {
        val id = seedEarning(
            amount = BigDecimal("3200.00"),
            competencyMonth = "2026-09",
            notes = "Plantão noturno",
        )

        val result = service.update(
            id,
            EarningUpdateRequest(notes = "Plantão noturno - revisado"),
            operator,
        )

        assertEquals(0, BigDecimal("3200.00").compareTo(result.amount))
        assertEquals("2026-09", result.competencyMonth)
        assertEquals("Plantão noturno - revisado", result.notes)
    }

    @Test
    fun `create persists a new earning for the practitioner`() {
        val created = service.create(
            EarningCreateRequest(
                practitionerId = practitionerId.toString(),
                amount = "7200.00",
                competencyMonth = "2026-11",
                notes = "Repasse extra",
            ),
            operator,
        )

        assertEquals(practitionerId, created.practitionerId)
        assertEquals(0, BigDecimal("7200.00").compareTo(created.amount))
        assertEquals("2026-11", created.competencyMonth)

        val persisted = repository.findById(created.id)
        assertNotNull(persisted)
        assertEquals(0, BigDecimal("7200.00").compareTo(persisted!!.amount))
    }

    @Test
    fun `listByPractitioner returns earnings scoped to the practitioner`() {
        val otherPractitioner = UUID.fromString("22222222-2222-2222-2222-222222222222")
        seedEarning(amount = BigDecimal("5000.00"), competencyMonth = "2026-09")
        seedEarning(amount = BigDecimal("5100.00"), competencyMonth = "2026-10")
        repository.save(
            Earning(
                id = UUID.randomUUID(),
                practitionerId = otherPractitioner,
                amount = BigDecimal("4200.00"),
                competencyMonth = "2026-10",
                notes = null,
                updatedBy = operator.id,
                updatedAt = Instant.now(),
                createdAt = Instant.now(),
            )
        )

        val results = service.listByPractitioner(practitionerId)

        assertEquals(2, results.size)
        results.forEach { assertEquals(practitionerId, it.practitionerId) }
        assertTrue(results.any { it.competencyMonth == "2026-10" })
    }
}
