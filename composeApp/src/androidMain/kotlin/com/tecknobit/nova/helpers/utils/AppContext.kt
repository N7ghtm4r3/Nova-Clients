package com.tecknobit.nova.helpers.utils

import android.app.Application
import android.content.Context

internal object AppContext {

    private lateinit var application: Application

    fun setUp(context: Context) {
        application = context as Application
    }

    fun get(): Context {
        if(AppContext::application.isInitialized.not()) throw Exception("Application context isn't initialized")
        return application.applicationContext
    }

}