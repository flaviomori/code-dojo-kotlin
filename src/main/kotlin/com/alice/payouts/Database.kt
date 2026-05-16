package com.alice.payouts

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database as ExposedDatabase

object Database {
    fun init() {
        val ds = HikariDataSource(HikariConfig().apply {
            jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/payouts"
            username = System.getenv("DATABASE_USER") ?: "payouts"
            password = System.getenv("DATABASE_PASSWORD") ?: "payouts"
            maximumPoolSize = 10
        })
        Flyway.configure().dataSource(ds).load().migrate()
        ExposedDatabase.connect(ds)
    }
}
