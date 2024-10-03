package com.tecknobit.nova.helpers.storage

import androidx.sqlite.db.SupportSQLiteDatabase
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.android.AndroidSqliteDriver
import com.tecknobit.nova.cache.Nova
import com.tecknobit.nova.helpers.utils.AppContext
import com.tecknobit.novacore.helpers.LocalSessionUtils.DATABASE_NAME

/**
 * The **DatabaseDriverFactory** class is useful to create the specific database driver for each
 * platform
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class DatabaseDriverFactory {

    /**
     * Function to create the driver
     *
     * No-any params required
     *
     * @return the driver specific for each platform as [SqlDriver]
     */
    actual fun createDriver(): SqlDriver {
        val schema = Nova.Schema
        return AndroidSqliteDriver(
            schema = schema,
            context = AppContext.get(),
            name = DATABASE_NAME,
            callback = object : AndroidSqliteDriver.Callback(schema) {
                override fun onOpen(db: SupportSQLiteDatabase) {
                    db.execSQL("PRAGMA foreign_keys=ON;")
                }
            }
        )
    }

}