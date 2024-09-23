package com.tecknobit.nova.helpers.storage

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.tecknobit.nova.cache.Nova
import com.tecknobit.novacore.helpers.LocalSessionUtils.DATABASE_NAME
import kotlinx.coroutines.runBlocking
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory {

    companion object {

        /**
         * `DATABASE_PATH` the path where store the local database
         */
        private val DATABASE_PATH: String = "jdbc:sqlite:" + System.getenv("APPDATA") + File.pathSeparator + DATABASE_NAME

    }

    private lateinit var driver: SqlDriver

    actual fun createDriver(): SqlDriver {
        driver = JdbcSqliteDriver(DATABASE_PATH)
            .also {
                runBlocking {
                    Nova.Schema.create(it).await()
                }
            }
        return driver
    }

}