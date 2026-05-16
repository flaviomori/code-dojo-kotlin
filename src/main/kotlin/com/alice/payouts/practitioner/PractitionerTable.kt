package com.alice.payouts.practitioner

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.timestamp

object PractitionerTable : Table("practitioners") {
    val id = uuid("id")
    val fullName = text("full_name")
    val crm = text("crm")
    val specialty = text("specialty")
    val createdAt = timestamp("created_at")

    override val primaryKey = PrimaryKey(id)
}
