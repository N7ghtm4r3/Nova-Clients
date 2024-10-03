package com.tecknobit.nova.helpers.storage

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import com.tecknobit.nova.cache.Nova
import com.tecknobit.novacore.helpers.LocalSessionUtils.DATABASE_NAME
import kotlinx.coroutines.runBlocking
import java.io.File

/**
 * The **DatabaseDriverFactory** class is useful to create the specific database driver for each
 * platform
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory {

    companion object {

        /**
         * `DATABASE_PATH` the path where store the local database
         */
        private val DATABASE_PATH: String = "jdbc:sqlite:" + System.getenv("APPDATA") + File.pathSeparator + DATABASE_NAME

    }

    /**
     * Function to create the driver
     *
     * No-any params required
     *
     * @return the driver specific for each platform as [SqlDriver]
     */
    actual fun createDriver(): SqlDriver {
        return JdbcSqliteDriver(DATABASE_PATH)
            .also {
                runBlocking {
                    Nova.Schema.create(it).await()
                }
            }
    }

}