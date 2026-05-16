package com.alice.payouts.paymentsplit

import com.alice.payouts.auth.AuthOperator
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID
import org.jetbrains.exposed.sql.Database as ExposedDatabase

@Testcontainers
class PaymentSplitServiceTest {

    companion object {
        @Container
        @JvmStatic
        val postgres = PostgreSQLContainer("postgres:16-alpine").apply {
            withDatabaseName("payouts")
            withUsername("payouts")
            withPassword("payouts")
        }

        @JvmStatic
        @BeforeAll
        fun setupDatabase() {
            postgres.start()
            val ds = HikariDataSource(HikariConfig().apply {
                jdbcUrl = postgres.jdbcUrl
                username = postgres.username
                password = postgres.password
                maximumPoolSize = 4
            })
            Flyway.configure().dataSource(ds).load().migrate()
            ExposedDatabase.connect(ds)
        }
    }

    private val repository = PaymentSplitRepository()
    private val service = PaymentSplitService(repository)
    private val practitionerId = UUID.fromString("11111111-1111-1111-1111-111111111111")
    private val bankAccountId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
    private val operator = AuthOperator(UUID.fromString("99999999-9999-9999-9999-999999999999"))

    @BeforeEach
    fun cleanPaymentSplits() {
        transaction {
            PaymentSplitTable.deleteAll()
        }
    }

    private fun seedSplit(
        sharePercentage: BigDecimal = BigDecimal("60.00"),
        procedureRef: String? = "Cirurgia cardiovascular #2026-10-014",
        notes: String? = null,
    ): UUID {
        val now = Instant.now()
        val split = PaymentSplit(
            id = UUID.randomUUID(),
            practitionerId = practitionerId,
            bankAccountId = bankAccountId,
            sharePercentage = sharePercentage,
            procedureRef = procedureRef,
            notes = notes,
            registeredBy = operator.id,
            updatedBy = operator.id,
            updatedAt = now,
            createdAt = now,
        )
        repository.save(split)
        return split.id
    }

    @Test
    fun `update changes the share percentage when provided`() {
        val id = seedSplit(sharePercentage = BigDecimal("60.00"))

        val result = service.update(
            id,
            PaymentSplitUpdateRequest(sharePercentage = "40.00"),
            operator,
        )

        assertEquals(0, BigDecimal("40.00").compareTo(result.sharePercentage))
        val persisted = repository.findById(id)
        assertNotNull(persisted)
        assertEquals(0, BigDecimal("40.00").compareTo(persisted!!.sharePercentage))
    }

    @Test
    fun `update preserves untouched fields`() {
        val id = seedSplit(
            sharePercentage = BigDecimal("55.50"),
            procedureRef = "Consulta ambulatorial #2026-09-002",
            notes = null,
        )

        val result = service.update(
            id,
            PaymentSplitUpdateRequest(notes = "Revisado pelo financeiro"),
            operator,
        )

        assertEquals(0, BigDecimal("55.50").compareTo(result.sharePercentage))
        assertEquals("Consulta ambulatorial #2026-09-002", result.procedureRef)
        assertEquals("Revisado pelo financeiro", result.notes)
    }

    @Test
    fun `create persists a new payment split for the practitioner`() {
        val created = service.create(
            PaymentSplitCreateRequest(
                practitionerId = practitionerId.toString(),
                bankAccountId = bankAccountId.toString(),
                sharePercentage = "72.50",
                procedureRef = "Procedimento eletivo #2026-11-007",
            ),
            operator,
        )

        assertEquals(practitionerId, created.practitionerId)
        assertEquals(bankAccountId, created.bankAccountId)
        assertEquals(0, BigDecimal("72.50").compareTo(created.sharePercentage))

        val persisted = repository.findById(created.id)
        assertNotNull(persisted)
        assertEquals(0, BigDecimal("72.50").compareTo(persisted!!.sharePercentage))
    }

    @Test
    fun `listByPractitioner returns payment splits scoped to the practitioner`() {
        val otherPractitioner = UUID.fromString("22222222-2222-2222-2222-222222222222")
        val otherAccount = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd")
        seedSplit(sharePercentage = BigDecimal("60.00"))
        seedSplit(sharePercentage = BigDecimal("40.00"), procedureRef = "Outra cirurgia")
        repository.save(
            PaymentSplit(
                id = UUID.randomUUID(),
                practitionerId = otherPractitioner,
                bankAccountId = otherAccount,
                sharePercentage = BigDecimal("100.00"),
                procedureRef = "Procedimento de outro prestador",
                notes = null,
                registeredBy = operator.id,
                updatedBy = operator.id,
                updatedAt = Instant.now(),
                createdAt = Instant.now(),
            )
        )

        val results = service.listByPractitioner(practitionerId)

        assertEquals(2, results.size)
        results.forEach { assertEquals(practitionerId, it.practitionerId) }
    }
}
