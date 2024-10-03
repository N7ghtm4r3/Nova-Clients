package com.tecknobit.nova.helpers.storage

import app.cash.sqldelight.db.SqlDriver

/**
 * The **DatabaseDriverFactory** class is useful to create the specific database driver for each
 * platform
 *
 * @author N7ghtm4r3 - Tecknobit
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect class DatabaseDriverFactory {

    /**
     * Function to create the driver
     *
     * No-any params required
     *
     * @return the driver specific for each platform as [SqlDriver]
     */
    fun createDriver(): SqlDriver

}