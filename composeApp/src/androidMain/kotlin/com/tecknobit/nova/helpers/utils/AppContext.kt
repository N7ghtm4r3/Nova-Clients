package com.tecknobit.nova.helpers.utils

import android.app.Application
import android.content.Context

/**
 * Utility object that manages the application's `Context` across the app lifecycle.
 *
 * The `AppContext` object allows for safe access to the application's global `Context` throughout the app.
 * It ensures that the `Context` is initialized properly before being used by other components, preventing
 * potential issues like null pointer exceptions or uninitialized access.
 *
 * ## Usage:
 * - Call `setUp(context: Context)` once, typically in the `onCreate()` method of the `Application` class,
 *   passing the application `Context`.
 * - Access the application `Context` anywhere in the app using `AppContext.get()`, but only after `setUp()`
 *   has been called. If you attempt to call `get()` before setup, an exception will be thrown.
 *
 * @throws Exception if the `Context` is accessed before initialization via `setUp`.
 */
internal object AppContext {

    /**
     * **application** -> instance for the app, initialized via `setUp()` method
     */
    private lateinit var application: Application

    /**
     * Initializes the `AppContext` with the provided `Context`.
     *
     * This method should be called once in the application's lifecycle, usually
     * in the `onCreate()` method of the `Application` class.
     *
     * @param context: the application `Context`, typically cast from the `Context` of the calling component.
     */
    fun setUp(context: Context) {
        application = context as Application
    }

    /**
     * Provides the application-wide `Context`.
     *
     * If the context has not been initialized via `setUp()`, this method will throw an `Exception`.
     *
     * @return the application `Context`.
     * @throws Exception if the context is not yet initialized.
     */
    fun get(): Context {
        if(AppContext::application.isInitialized.not()) throw Exception("Application context isn't initialized")
        return application.applicationContext
    }

}