package com.aiblooddiagnostics.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource

object DatabaseFactory {
    private lateinit var dataSource: HikariDataSource
    
    fun init() {
        Database.connect(hikari())
    }
    
    private fun hikari(): HikariDataSource {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = System.getenv("DATABASE_URL") ?: "jdbc:postgresql://localhost:5432/blood_diagnostics"
            username = System.getenv("DATABASE_USER") ?: "aiblood_user"
            password = System.getenv("DATABASE_PASSWORD") ?: "aiblood_pass123"
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}

fun Application.configureDatabase() {
    DatabaseFactory.init()
    log.info("Database connected successfully!")
}
