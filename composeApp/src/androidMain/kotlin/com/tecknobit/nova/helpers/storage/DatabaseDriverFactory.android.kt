package com.tecknobit.nova.helpers.storage

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tecknobit.nova.cache.Nova
import com.tecknobit.nova.helpers.utils.AppContext

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory {

    private lateinit var driver: SqlDriver

    actual fun createDriver(): SqlDriver {
        val schema = Nova.Schema
        driver = AndroidSqliteDriver(
            schema = schema,
            context = AppContext.get(),
            name = "Nova.db",
            callback = object : AndroidSqliteDriver.Callback(schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            }
        )
        return driver
    }

}