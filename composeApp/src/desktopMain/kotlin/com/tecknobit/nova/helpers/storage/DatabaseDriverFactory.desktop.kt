package com.tecknobit.nova.helpers.storage

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.tecknobit.nova.cache.Nova

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory {

    private lateinit var driver: SqlDriver

    actual fun createDriver(): SqlDriver {
        driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
            .also { Nova.Schema.create(it) }
        return driver
    }

}