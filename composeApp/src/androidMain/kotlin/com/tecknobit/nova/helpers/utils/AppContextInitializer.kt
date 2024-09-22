package com.tecknobit.nova.helpers.utils

import android.content.Context
import androidx.startup.Initializer

internal class AppContextInitializer : Initializer<Context> {

    override fun create(context: Context): Context {
        AppContext.setUp(context.applicationContext)
        return AppContext.get()
    }

    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}